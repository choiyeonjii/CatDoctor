package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_hospital_review.*
import java.io.Serializable

class HospitalReviewActivity : AppCompatActivity() {

    private var usernickname=""
    private var userid=""

    var name=""
    var road=""
    var call=""

    data class Review(var nickname: String, var star: String, var title: String, var content: String, var date: String): Serializable
    val firebasedatabase = Firebase.database
    val myRef=firebasedatabase.getReference("review")
    private var review = ArrayList<Review>()

    var reviewAdapter = ReviewAdapter(review) // 리사이클러 뷰 어댑터

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_review)



        userid=intent.getStringExtra("id").toString()
        usernickname=intent.getStringExtra("nickname").toString()
        name = intent.getStringExtra("name").toString()
        road = intent.getStringExtra("road").toString()
        call = intent.getStringExtra("call").toString()
        //review = intent.getSerializableExtra("review") as ArrayList<Review>
        Log.v("받아온 리뷰 데이터", review.toString())
        val h_name= findViewById<TextView>(R.id.hospital_name) as TextView
        val h_road= findViewById<TextView>(R.id.hospital_road) as TextView
        val h_call= findViewById<TextView>(R.id.hospital_call) as TextView

        val intent=intent

        h_name.text=name
        h_road.text=road
        h_call.text=call



        readReview(name,road)

        hospital_name.setOnClickListener {
            review_list.adapter=reviewAdapter
            val lm=LinearLayoutManager(this)
            review_list.layoutManager = lm
            review_list.setHasFixedSize(true)
        }
        button_write_review.setOnClickListener {
            val intent = Intent(applicationContext, ReviewActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("road", road)
            intent.putExtra("id",userid)
            intent.putExtra("nickname",usernickname)
            Log.v("리뷰데이터5",review.toString())
            startActivity(intent)
        }

    }
    private fun readReview(name : String, road: String) {
        //val myRef =firebasedatabase.getReference("review/"+name+"/"+road)

        myRef.addValueEventListener(object: ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            //Review(var nickname: String, var star: String, var title: String, var content: String, var date: String)
            override fun onDataChange(snapshot: DataSnapshot) {
                review.clear()
                var getReviewInfo=snapshot
                getReviewInfo=getReviewInfo.child(name)
                getReviewInfo=getReviewInfo.child(road)
                for (u in getReviewInfo.children) {
                    val user_id: String = u.key.toString()
                    val content: String = u.child("content").value as String
                    val star: String = u.child("star").value as String
                    val title: String = u.child("title").value as String
                    val date: String = u.child("date").value as String
                    review.add(Review(usernickname, star, title, content, date))
                }
                Log.v("병원리뷰데이터1",review.toString())

            }
        })

    }

}