package kr.ac.kumoh.s20181180.catdoctor

// 참고자료 : https://colab.research.google.com/drive/135lSP5ttRFtgBlAE2P81oSBE1zMJNhSJ#scrollTo=etvPtH9l2QfU

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
    }

    private lateinit var mQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mQueue = Volley.newRequestQueue(this)

        requestGundam()
    }

    override fun onStop() {
        super.onStop()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun requestGundam() {
        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        val url = "http://192.168.0.15:8080/symptom"

        val request = JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                {
                    txResult.text = it.toString()
                },
                {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    txResult.text = it.toString()
                }
        )

        request.tag = QUEUE_TAG
        mQueue.add(request)
    }
}