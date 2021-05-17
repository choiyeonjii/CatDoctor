package kr.ac.kumoh.s20181180.catdoctor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class DiagnoseViewModel(application: Application) : AndroidViewModel(application)  {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
    }
    val server_url = "http://192.168.0.15:8080"

    private lateinit var mQueue: RequestQueue

    data class Disease (var id: Int, var name: String, var define: String, var cause: String, var treatment: String, var prenention: String, var prognosis: String, var advice: String)


    val list = MutableLiveData<ArrayList<Disease>>()
    private val disease = ArrayList<Disease>()

    private val disease_id = ArrayList<Int>()

    init {
        list.value = disease
        mQueue = Volley.newRequestQueue(application)
    }

    fun requestDisease(symptom_id: ArrayList<Int>) {
        requestDiseaseID(symptom_id)
        Log.i("last_disease_id", disease_id.toString())

        for (i in 0 until disease_id.size) {
            val url = "$server_url/disease?id=${disease_id[i]}"

            val request = JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    {
                        disease.clear()
                        parseJson_Disease(it)
                        list.value = disease
                    },
                    {
                    }
            )
            request.tag = QUEUE_TAG
            mQueue.add(request)
        }
    }

    fun requestDiseaseID(symptom_id: ArrayList<Int>) {
        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        disease_id.clear()
        for (i in 0 until symptom_id.size) {
            val url = "$server_url/disease_id?symptom_id=${symptom_id[i]}"
            Log.i("symptom_id", symptom_id[i].toString())
            Log.i("disease_url", url)

            val request = JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    {
                        parseJson_DiseaseId(it)
                    },
                    {
                    }
            )
            request.tag = QUEUE_TAG
            mQueue.add(request)
        }
    }

    fun getDisease(i: Int) = disease[i]

    fun getDiseaseSize() = disease.size

    override fun onCleared() {
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun parseJson_Disease(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("id")
            val name = item.getString("disease_name")
            val define = item.getString("define")
            val cause = item.getString("cause")
            val treatment = item.getString("treatment")
            val prevention = item.getString("prevention")
            val prognosis = item.getString("prognosis")
            val advice = item.getString("advice")

            disease.add(Disease(id, name, define, cause, treatment, prevention, prognosis, advice))
        }
    }

    private fun parseJson_DiseaseId(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("id")
            disease_id.add(id)
        }
        Log.i("select_disease_id_size", items.length().toString())
        Log.i("select_disease_id33", disease_id.toString())
        Log.i("select_disease_id44", disease_id.size.toString())
    }
}