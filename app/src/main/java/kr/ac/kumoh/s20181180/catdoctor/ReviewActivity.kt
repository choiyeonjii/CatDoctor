package kr.ac.kumoh.s20181180.catdoctor

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_review.*
import java.time.LocalDate

class ReviewActivity : AppCompatActivity() {

    private var userid=""
    private var usernickname=""
    private var rt:Long=0
    val firebasedatabase = Firebase.database

    var ratingcheck=0
    var titlecheck=0
    var contentcheck=0
    companion object {
        var myRef : DatabaseReference? = null
    }
    private lateinit var database: DatabaseReference
    data class Review(var nickname: String, var star: Long, var title: String, var content: String, var date: String)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        database = Firebase.database.reference

        userid=intent.getStringExtra("id").toString()
        usernickname=intent.getStringExtra("nickname").toString()


        val intent=intent

        val name = intent.getStringExtra("name").toString()
        val road = intent.getStringExtra("road").toString()

        myRef=firebasedatabase.getReference("review/"+name+"/"+road)
        readReview()
        review_star.setOnRatingBarChangeListener{ ratingBar, rating, fromUser ->
            review_star.rating=rating
            rt= rating.toLong()
        }
        reg_review_btn.setOnClickListener {
            if(review_star.rating<1){
                Toast.makeText(this,"평점은 1점 이상 입력 가능합니다.",Toast.LENGTH_SHORT).show()
            }
            else if(review_title.text.length<1){
                Toast.makeText(this,"한줄평을 입력해주세요.",Toast.LENGTH_SHORT).show()
            }
            else if(review_content.text.length<20){
                Toast.makeText(this,"최소 20자 이상 입력해주세요.",Toast.LENGTH_SHORT).show()
            }
            else{
                writeReview(userid,usernickname,name,road)
                finish()
            }

        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun writeReview(user_id:String, nickname:String, name: String, road:String) {

        val onlyDate: LocalDate = LocalDate.now()
        val titleeditText = findViewById<View>(R.id.review_title) as EditText
        val contenteditText = findViewById<View>(R.id.review_content) as EditText
        val newReview= Review(nickname, rt, titleeditText.text.toString(), contenteditText.text.toString(),onlyDate.toString())
        database.child("review").child(name).child(road).child(user_id).setValue(newReview)
    }

    private fun readReview() {
        myRef?.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            //Review(var nickname: String, var star: String, var title: String, var content: String, var date: String)
            override fun onDataChange(snapshot: DataSnapshot) {
                val getUserInfo=snapshot
                for (u in getUserInfo.children){
                    val user_id: String=u.key.toString()
                    val content: String=u.child("content").value as String
                    val star: Long =u.child("star").value as Long
                    val title: String=u.child("title").value as String

                    if(user_id==userid){
                        val r_title = findViewById<View>(R.id.review_title) as EditText
                        val r_content = findViewById<View>(R.id.review_content) as EditText
                        val r_star=findViewById<View>(R.id.review_star) as RatingBar
                        r_title.setText(title)
                        r_content.setText(content)
                        r_star.rating=star.toFloat()
                    }
                }
            }
        })
    }
}