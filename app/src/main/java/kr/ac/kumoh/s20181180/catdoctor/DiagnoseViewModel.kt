package kr.ac.kumoh.s20181180.catdoctor

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.collection.LruCache
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import kotlin.math.log

class DiagnoseViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val CLASSIFY = "classify"
        const val QUEUE_TAG1 = "VolleyRequest1"
        const val QUEUE_TAG2 = "VolleyRequest2"
    }
    private lateinit var mQueue1: RequestQueue
    private lateinit var mQueue2: RequestQueue
    val server_url = "http://192.168.0.12:8080"

    data class SymptomClassify (var classify: String, var image: String)
    val list1 = MutableLiveData<ArrayList<SymptomClassify>>()
    private var symptomclassify = ArrayList<SymptomClassify>()

    data class Symptom (var id: Int, var classify: String, var code: String, var name: String)
    val list2 = MutableLiveData<ArrayList<Symptom>>()
    private val symptom = ArrayList<Symptom>()


    init {
        list1.value = symptomclassify
        list2.value = symptom
        mQueue1 = Volley.newRequestQueue(application)
        mQueue2 = Volley.newRequestQueue(application)
    }

    fun requestSymptomClassify() {
        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        val url = "http://192.168.0.12:8080/symptom_distinct"

        val request = JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                {
//                    Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                    symptom.clear()
                    parseJson1(it)
                    list1.value = symptomclassify
                },
                {
//                    Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                }
        )

        request.tag = QUEUE_TAG1
        mQueue1.add(request)
    }

    fun requestSymptom() {
        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        val url = "http://192.168.0.12:8080/symptom"

        val request = JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                {
//                    Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                    symptom.clear()
                    parseJson2(it)
                    list2.value = symptom
                },
                {
//                    Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                }
        )

        request.tag = QUEUE_TAG2
        mQueue2.add(request)
    }

    val imageLoader: ImageLoader
    init {
        list1.value = symptomclassify
        mQueue1 = Volley.newRequestQueue(application)

        imageLoader = ImageLoader(mQueue1,
                object : ImageLoader.ImageCache {
                    private val cache = LruCache<String, Bitmap>(100)
                    override fun getBitmap(url: String): Bitmap? {
                        return cache.get(url)
                    }
                    override fun putBitmap(url: String, bitmap: Bitmap) {
                        cache.put(url, bitmap)
                    }
                })
    }

    fun getImageUrl(i: Int): String = "$server_url/image/" + symptomclassify[i].image

    fun getSymptom(i: Int) = symptom[i]

    fun getSymptomSize() = symptom.size

    fun getSymptomClassify(i: Int) = symptomclassify[i]

    fun getSymptomClassifySize() = symptomclassify.size

    override fun onCleared() {
        super.onCleared()
        mQueue1.cancelAll(QUEUE_TAG1)
        mQueue2.cancelAll(QUEUE_TAG2)
    }

    private fun parseJson1(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val classify = item.getString("symptom_classify")
            val image = item.getString("image")
            symptomclassify.add(SymptomClassify(classify, image))
            Log.i("symptomclassify", symptomclassify.toString())
        }
    }

    private fun parseJson2(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("id")
            val classify = item.getString("symptom_classify")
            val code = item.getString("symptom_code")
            val name = item.getString("symptom_name")
            symptom.add(Symptom(id, classify, code, name))
        }
    }
}