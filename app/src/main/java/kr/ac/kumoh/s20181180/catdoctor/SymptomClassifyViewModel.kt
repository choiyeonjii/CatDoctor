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
import kr.ac.kumoh.s20181180.catdoctor.MainActivity.Companion.SERVER_URL
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import kotlin.math.log



class SymptomClassifyViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val CLASSIFY = "classify"
        const val QUEUE_TAG = "VolleyRequest"
    }

    private lateinit var mQueue: RequestQueue

    data class SymptomClassify(var classify: String, var image: String)

    val list = MutableLiveData<ArrayList<SymptomClassify>>()
    private var symptomclassify = ArrayList<SymptomClassify>()
    

    init {
        list.value = symptomclassify
        mQueue = Volley.newRequestQueue(application)
    }

    fun requestSymptomClassify() {
        val url = "$SERVER_URL/symptom_distinct"

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            {
                //Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                symptomclassify.clear()
                parseJson(it)
                list.value = symptomclassify
            },
            {
                //Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )

        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

    val imageLoader: ImageLoader

    init {
        list.value = symptomclassify
        mQueue = Volley.newRequestQueue(application)

        imageLoader = ImageLoader(mQueue,
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

    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" + symptomclassify[i].image

    fun getSymptomClassify(i: Int) = symptomclassify[i]

    fun getSymptomClassifySize() = symptomclassify.size

    override fun onCleared() {
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun parseJson(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val classify = item.getString("symptom_classify")
            val image = item.getString("image")
            symptomclassify.add(SymptomClassify(classify, image))
            Log.i("symptomclassify", symptomclassify.toString())
        }
    }
}