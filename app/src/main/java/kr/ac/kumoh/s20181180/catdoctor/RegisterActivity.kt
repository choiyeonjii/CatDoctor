package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kr.ac.kumoh.s20181180.catdoctor.MainActivity.Companion.SERVER_URL
import org.json.JSONArray
import org.json.JSONObject


class RegisterActivity : AppCompatActivity() {
    val url = "$SERVER_URL/insert"
    private var check=0
    private var able=0
    private lateinit var mQueue: RequestQueue
    private var info = ArrayList<LoginActivity.Info>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        info = intent.getSerializableExtra("info") as ArrayList<LoginActivity.Info>
        Log.i("info", info.toString())

        mQueue = Volley.newRequestQueue(this)
        //requestInfo()

        id.addTextChangedListener {
            check=10
        }
        checkbutton.setOnClickListener {
            check=0
            for (i in 0 until info.size) {
                if(info[i].user_id.equals(id.text.toString())) {
                    check+=1
                }
            }
            if(check==0) {
                if (id.text.toString().equals(""))
                    Toast.makeText(this, "아이디를 입력해주십시오", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, "사용 가능한 아이디 입니다", Toast.LENGTH_LONG).show()
            }
            else
                Toast.makeText(this, "중복된 아이디 입니다", Toast.LENGTH_LONG).show()
        }

        registerbtn.setOnClickListener {
            able=0
            if(id.text.toString().equals("") || psword.text.toString().equals("") || pswordcheck.text.toString().equals("") || name.text.toString().equals("") || nickname.text.toString().equals("")) {
                able+=1
                Toast.makeText(this, "빈칸이 존재합니다", Toast.LENGTH_LONG).show()
            }
            else {
                //비밀번호 체크
                if (psword.text.toString().equals(pswordcheck.text.toString()))
                    able += 0
                else {
                    able += 1
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show()
                }
                //아이디 중복 체크
                if (check != 0)
                    Toast.makeText(this, "아이디 중복 체크가 필요합니다", Toast.LENGTH_LONG).show()
            }
            if(check==0)
                if(able==0)
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