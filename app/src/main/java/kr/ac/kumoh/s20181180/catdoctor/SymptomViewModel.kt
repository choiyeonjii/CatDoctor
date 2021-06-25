package kr.ac.kumoh.s20181180.catdoctor

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kr.ac.kumoh.s20181180.catdoctor.MainActivity.Companion.SERVER_URL
import org.json.JSONArray
import org.json.JSONObject

class SymptomViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val SYMPTOM = "Symptom"
        const val QUEUE_TAG = "VolleyRequest"
    }

    private lateinit var mQueue: RequestQueue

    data class Symptom (var id: Int, var classify: String, var code: String, var name: String)

    val list = MutableLiveData<ArrayList<Symptom>>()
    val symptom = ArrayList<Symptom>()


    init {
        list.value = symptom
        mQueue = Volley.newRequestQueue(application)
    }

    fun requestSymptom(classify: String) {
        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        val url = "$SERVER_URL/symptom_classify?symptom_classify='${classify}'"

        val request = JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                {
                    symptom.clear()
                    parseJson(it)
                    list.value = symptom
                },
                {
                    Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                }
        )

        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

    fun getSymptom(i: Int) = symptom[i]

    fun getSymptomSize() = symptom.size

    override fun onCleared() {
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun parseJson(items: JSONArray) {
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