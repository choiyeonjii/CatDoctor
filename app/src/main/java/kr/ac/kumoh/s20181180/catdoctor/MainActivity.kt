package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        const val SERVER_URL = "http://192.168.0.15:8080"
    }

    private var kakao=0
    private var google=0
    private var normal=0
    private var userid=""
    private var usernickname=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        kakao = intent.getIntExtra("kakao", kakao)
        google = intent.getIntExtra("google", google)
        normal = intent.getIntExtra("normal", normal)
        userid=intent.getStringExtra("id").toString()
        usernickname=intent.getStringExtra("nickname").toString()

        diagnose_btn.setOnClickListener {
            startActivity(Intent(this, SelectDiagnoseTypeActivity::class.java))
        }

        mypage_btn.setOnClickListener {
            intent = Intent(this, MypageActivity::class.java)
            intent.putExtra("kakao", kakao)
            intent.putExtra("google", google)
            intent.putExtra("normal", normal)
            intent.putExtra("id",userid)
            intent.putExtra("nickname",usernickname)
            startActivity(intent)
            //finish()
        }
        hospital_btn.setOnClickListener {
            intent = Intent(this, MapActivity::class.java)
            intent.putExtra("kakao", kakao)
            intent.putExtra("google", google)
            intent.putExtra("normal", normal)
            intent.putExtra("id",userid)
            intent.putExtra("nickname",usernickname)
            startActivity(intent)
        }
    }
}