package kr.ac.kumoh.s20181180.catdoctor

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class SymptomClassifyViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val CLASSIFY = "classify"
        const val QUEUE_TAG = "VolleyRequest"
    }
    private lateinit var mQueue: RequestQueue

    val list = MutableLiveData<ArrayList<String>>()
    private var symptomclassify = ArrayList<String>()


    init {
        list.value = symptomclassify
        mQueue = Volley.newRequestQueue(application)
    }

    fun requestSymptomClassify() {
        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        val url = "http://192.168.0.15:8080/symptom_distinct"

        val request = JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                {
                    Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                    symptomclassify.clear()
                    parseJson(it)
                    list.value = symptomclassify
                },
                {
                    Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                }
        )

        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

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
            symptomclassify.add(classify)
        }
    }

}