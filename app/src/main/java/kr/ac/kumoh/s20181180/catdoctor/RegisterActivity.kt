package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONArray
import org.json.JSONObject


class RegisterActivity : AppCompatActivity() {
    val url = "http://192.168.0.6:8080/insert"
    private lateinit var mQueue: RequestQueue

    data class Info(var id: Int, var user_id: String, var password: String, var name: String, var nickname: String)
    private var info = ArrayList<LoginActivity.Info>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        info = intent.getSerializableExtra("info") as ArrayList<LoginActivity.Info>
        Log.i("info", info.toString())

        mQueue = Volley.newRequestQueue(this)
        //requestInfo()

        registerbtn.setOnClickListener {

            finish()
        }
    }
    private fun requestInfo() {
        val request = JsonArrayRequest(
            Request.Method.POST,
            url,
            null,
            {
                //Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            },
            {
                Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )
        request.tag = "VolleyRequest"
        mQueue.add(request)
    }

}