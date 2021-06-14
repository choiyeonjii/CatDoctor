
package kr.ac.kumoh.s20181180.catdoctor

import android.app.Application
import android.util.Log
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
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DiagnoseViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
        const val DISEASE_ID = "DiseaseId"
        const val SYMPTOM_ID = "SymptomId"
    }

    private lateinit var mQueue: RequestQueue

    data class Disease(var id: Int, var name: String, var define: String, var cause: String, var treatment: String, var prenention: String, var prognosis: String, var advice: String)

    val list = MutableLiveData<ArrayList<Disease>>()
    private val disease = ArrayList<Disease>()

    var disease_id = ArrayList<Int>()

    var diseaseMap = HashMap<Int, Int>()
    var keySetList = ArrayList<Int>(diseaseMap.size)


    init {
        list.value = disease
        mQueue = Volley.newRequestQueue(application)
    }

    fun requestDisease(disease_id: ArrayList<Int>) {
        disease_id.sort()

        var count: Int = 1
        for (i in 0 until disease_id.size) {
            if (i != 0)
                if (disease_id[i] != disease_id[i-1])
                    count = 1
            diseaseMap.put(disease_id[i], count++)
        }
        var diseaseSortedMap = diseaseMap.toList().sortedByDescending { (k, v) -> v }.toMap()

        Log.i("diseaseSortedMap", diseaseSortedMap.toString())
        disease_id.clear()

        for (i in diseaseSortedMap.keys) {
            if (diseaseSortedMap.get(i)!! > 1) {
                disease_id.add(i)
            }
        }


        var temp = disease_id.joinToString(
                separator = ", "
        )
        Log.i("last_disease_id", disease_id.toString())

        val url = "$SERVER_URL/disease?id=${temp}"
        Log.i("disease_url", url)

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
                    Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                }
        )
        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

    fun requestDiseaseID(symptom_id: ArrayList<Int>) {
        var temp = symptom_id.joinToString(
                prefix = "(",
                separator = ", ",
                postfix = ")"
        )
        val url = "$SERVER_URL/disease_id?symptom_id=${temp}"
        Log.i("disease_url", url)
        val request = JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                {
                    //Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                    disease_id.clear()
                    parseJson_DiseaseId(it)
                },
                {
                }
        )
        request.tag = QUEUE_TAG
        mQueue.add(request)
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
        Log.i("disease_id", disease_id.toString())
        Log.i("disease_id_size_sum", disease_id.size.toString())
        requestDisease(disease_id)
    }
}