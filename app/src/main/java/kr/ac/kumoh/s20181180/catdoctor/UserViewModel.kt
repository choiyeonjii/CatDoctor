package kr.ac.kumoh.s20181180.catdoctor

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlin.collections.ArrayList

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var database: DatabaseReference

    /*private lateinit var mQueue: RequestQueue
*/
    data class User(var user_id: String, var password: String, var name: String, var nickname: String)
    val list = MutableLiveData<ArrayList<UserViewModel.User>>()
    private val user = ArrayList<UserViewModel.User>()

    /*val list = MutableLiveData<ArrayList<Disease>>()*/

    /*var disease_id = ArrayList<Int>()

    var diseaseMap = HashMap<Int, Int>()
    var keySetList = ArrayList<Int>(diseaseMap.size)
*/

init {
    list.value = user
    database = Firebase.database.reference
    /*mQueue = Volley.newRequestQueue(application)*/
}
    private fun writeNewUser(id:String, user_id:String, password:String, name:String, nickname:String) {
        val newUser=User(user_id,password,name,nickname)
        user.add(newUser)
        database.child("user").child(id).child("user_id").setValue(newUser)

    }

/*fun requestDisease(disease_id: ArrayList<Int>) {
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
}*/
}