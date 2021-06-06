package kr.ac.kumoh.s20181180.catdoctor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_hospital_review.*

class HospitalReviewActivity : AppCompatActivity() {

    private var userid=""
    private var usernickname=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_review)

        userid=intent.getStringExtra("id").toString()
        usernickname=intent.getStringExtra("nickname").toString()

        val h_name= findViewById<TextView>(R.id.hospital_name) as TextView
        val h_road= findViewById<TextView>(R.id.hospital_road) as TextView
        val h_call= findViewById<TextView>(R.id.hospital_call) as TextView

        val intent=intent

        val name = intent.extras!!.getString("name")
        h_name.text=name

        val road = intent.extras!!.getString("road")
        h_road.text=road

        val call = intent.extras!!.getString("call")
        h_call.text=call

        button_write_review.setOnClickListener {
            val intent = Intent(applicationContext, ReviewActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("road", road)
            intent.putExtra("id",userid)
            intent.putExtra("nickname",usernickname)
            startActivity(intent)
        }

    }
}