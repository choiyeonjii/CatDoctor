package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.activity_main.txResult
import kotlinx.android.synthetic.main.activity_mypage.*

class MypageActivity : AppCompatActivity() {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
    }

    private lateinit var mQueue: RequestQueue
    private var kakao=0
    private var google=0
    private var normal=0

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)
        mQueue = Volley.newRequestQueue(this)

        prefs = this.getSharedPreferences("Prefs", 0)
        editor = prefs.edit()

        kakao = intent.getIntExtra("kakao", kakao)
        google = intent.getIntExtra("google", google)
        normal = intent.getIntExtra("normal", normal)
        Log.i("MainActivity: kakao", kakao.toString())
        Log.i("google", google.toString())
        Log.i("normal", normal.toString())

        logout_btn.setOnClickListener {
            if(kakao!=0) {
                // kakao 로그아웃
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.e("TAG", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                    } else {
                        kakao=0
                        editor.putInt("kakao", kakao).apply()
                        editor.commit()
                        Toast.makeText(this, "로그아웃 성공. SDK에서 토큰 삭제됨", Toast.LENGTH_LONG).show()
                        Log.i("TAG", "로그아웃 성공. SDK에서 토큰 삭제됨")
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
            }
            else if(normal!=0){
                normal=0
                editor.putInt("normal", normal).apply()
                editor.commit()
                Toast.makeText(this, "로그아웃 성공.", Toast.LENGTH_LONG).show()
                intent= Intent(this, LoginActivity::class.java)
                intent.putExtra("normal", normal)
                startActivity(intent)
                finish()
            }
            else if(google!=0){
                Firebase.auth.signOut()
                Log.v("logout","구글 로그아웃 성공")
                editor.putInt("google", google).apply()
                editor.commit()
                google=0
                intent= Intent(this, LoginActivity::class.java)
                intent.putExtra("google", google)
                startActivity(intent)
                finish()
            }
        }
        kakao_signout_btn.setOnClickListener{
            // 연결 끊기
            if(kakao!=0) {
                UserApiClient.instance.unlink { error ->
                    if (error != null) {
                        Log.e("TAG", "연결 끊기 실패", error)
                    }
                    else {
                        kakao=0
                        editor.putInt("kakao", kakao).apply()
                        editor.commit()
                        Toast.makeText(this, "연결 끊기 성공. SDK에서 토큰 삭제됨", Toast.LENGTH_LONG).show()
                        Log.i("TAG", "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
            }
            else if(google!=0){
                var user = Firebase.auth.currentUser;
                user.delete().addOnCompleteListener {
                    google=0
                    editor.putInt("google", google).apply()
                    editor.commit()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }

        }
    }

    override fun onStop() {
        super.onStop()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun requestGundam() {
        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        val url = "http://192.168.0.102:8080/symptom"

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