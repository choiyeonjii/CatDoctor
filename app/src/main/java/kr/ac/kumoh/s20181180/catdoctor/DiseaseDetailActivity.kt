package kr.ac.kumoh.s20181180.catdoctor

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_diseasedetail.*
import kr.ac.kumoh.s20181180.catdoctor.DiagnoseViewModel.Companion.DISEASE_ID
import kr.ac.kumoh.s20181180.catdoctor.DiagnoseViewModel.Companion.SYMPTOM_ID
import kr.ac.kumoh.s20181180.catdoctor.MainActivity.Companion.SERVER_URL
import org.json.JSONArray
import org.json.JSONObject

class DiseaseDetailActivity : AppCompatActivity() {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
    }
    lateinit var disease_id: String
    private lateinit var mQueue: RequestQueue
    private var symptom_id = ArrayList<Int>()

    data class Symptom (var id: Int, var name: String)
    val symptom = ArrayList<Symptom>()
    val symptom_name = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diseasedetail)

        disease_id = intent.getStringExtra(DISEASE_ID).toString()
        Log.i("disease_id2", disease_id)

        symptom_id = intent.getIntegerArrayListExtra(SYMPTOM_ID) as ArrayList<Int>

        mQueue = Volley.newRequestQueue(this)
        requestDisease()
        requestSymptom()
    }

    override fun onStop() {
        super.onStop()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun requestDisease() {
        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        val url = "${SERVER_URL}/disease?id=(${disease_id})"
        Log.i("disease_url2", url)

        val request = JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                {
                    parseJson_Disease(it)
                },
                {
                }
        )
        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

    private fun requestSymptom() {
        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        val url = "${SERVER_URL}/symptom_id?disease_id=(${disease_id})"
        Log.i("disease_url3", url)

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            {
                parseJson_Symptom(it)
            },
            {
            }
        )
        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

    private fun parseJson_Disease(items: JSONArray) {
        val item: JSONObject = items[0] as JSONObject
        val id = item.getInt("id")
        val name = item.getString("disease_name")
        val define = item.getString("define")
        val cause = item.getString("cause")
        val treatment = item.getString("treatment")
        val prevention = item.getString("prevention")
        val prognosis = item.getString("prognosis")
        val advice = item.getString("advice")
        diseaseName.text = name
        diseaseDefine.text = define
        diseaseCause.text = cause
        diseaseTreatment.text = treatment
        diseasePrevention.text = prevention
        diseasePrognosis.text = prognosis
        diseaseAdvice.text = advice
    }

    private fun parseJson_Symptom(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("id")
            val name = item.getString("symptom_name")
            symptom.add(Symptom(id, name))
            symptom_name.add(name)
        }
        var temp = symptom_name.joinToString(
            separator = " / "
        )
        // 참고자료 : https://developer.android.com/guide/topics/text/spans?hl=ko#kotlin
        val spannable = SpannableStringBuilder(temp)
        for (i in 0 until symptom.size){
            for (j in 0 until symptom_id.size) {
                if (symptom[i].id == symptom_id[j]) {
                    val word = symptom[i].name
                    val start = temp.indexOf(word)
                    val end = start + word.length

                    spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(BackgroundColorSpan(Color.parseColor("#FFE4E1")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            //        spannable.setSpan(ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        symptomlist.setText(spannable)
    }
}