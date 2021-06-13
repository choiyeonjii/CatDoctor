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
import kotlinx.android.synthetic.main.activity_hospital_review.*
import kotlinx.android.synthetic.main.activity_review.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class ReviewActivity : AppCompatActivity() {

    private var userid=""
    private var usernickname=""
    private var sitem=""
    private var writetype=0
    val firebasedatabase = Firebase.database

    companion object {
        var myRef : DatabaseReference? = null
    }
    private lateinit var database: DatabaseReference
    data class Review(var nickname: String, var star: String, var title: String, var content: String, var date: String)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        database = Firebase.database.reference

        userid=intent.getStringExtra("id").toString()
        usernickname=intent.getStringExtra("nickname").toString()
        writetype=intent.getIntExtra("type",0)

        val intent=intent

        val name = intent.getStringExtra("name").toString()
        val road = intent.getStringExtra("road").toString()

        myRef=firebasedatabase.getReference("review/"+name+"/"+road)
        readReview()
        reg_review_btn.setOnClickListener {
            writeReview(userid,usernickname,name,road)
            finish()
        }
        val spinner: Spinner = findViewById(R.id.review_star)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.star_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                var item = spinner.adapter.getItem(0)
                sitem=item.toString()
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // either one will work as well
                // val item = parent.getItemAtPosition(position) as String
                var item = spinner.adapter.getItem(position)
                sitem=item.toString()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun writeReview(user_id:String, nickname:String, name: String, road:String) {

        val onlyDate: LocalDate = LocalDate.now()
        val titleeditText = findViewById<View>(R.id.review_title) as EditText
        val contenteditText = findViewById<View>(R.id.review_content) as EditText
        val newReview= Review(nickname, sitem, titleeditText.text.toString(), contenteditText.text.toString(),onlyDate.toString())
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
                    val star: String=u.child("star").value as String
                    val title: String=u.child("title").value as String

                    if(user_id==userid){
                        val r_title = findViewById<View>(R.id.review_title) as EditText
                        val r_content = findViewById<View>(R.id.review_content) as EditText
                        val r_star=findViewById<View>(R.id.review_star) as Spinner
                        r_title.setText(title)
                        r_content.setText(content)
                        r_star.setSelection(star.toInt()-1)
                    }
                }
            }
        })
    }
}