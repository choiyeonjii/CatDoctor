package kr.ac.kumoh.s20181180.catdoctor

// 참고자료 : https://colab.research.google.com/drive/135lSP5ttRFtgBlAE2P81oSBE1zMJNhSJ#scrollTo=etvPtH9l2QfU

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        const val SERVER_URL = "http://192.168.0.105:8080"
    }

    private var kakao=0
    private var google=0
    private var normal=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        kakao = intent.getIntExtra("kakao", kakao)
        google = intent.getIntExtra("google", google)
        normal = intent.getIntExtra("normal", normal)

        diagnose_btn.setOnClickListener {
            startActivity(Intent(this, SymptomClassifyActivity::class.java))
        }

        mypage_btn.setOnClickListener {
            intent = Intent(this, MypageActivity::class.java)
            intent.putExtra("kakao", kakao)
            intent.putExtra("google", google)
            intent.putExtra("normal", normal)
            startActivity(intent)
            //finish()
        }
        hospital_btn.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
    }
}