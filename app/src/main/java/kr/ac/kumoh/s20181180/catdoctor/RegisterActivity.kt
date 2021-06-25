package kr.ac.kumoh.s20181180.catdoctor

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kr.ac.kumoh.s20181180.catdoctor.MainActivity.Companion.SERVER_URL


class RegisterActivity : AppCompatActivity() {
    val url = "$SERVER_URL/insert"
    private var check=0
    private var able=0
    private lateinit var mQueue: RequestQueue
    private var info = ArrayList<LoginActivity.Info>()
    private lateinit var database: DatabaseReference

    val firebasedatabase = Firebase.database
    val myRef=firebasedatabase.getReference("user")
    data class User(var user_id: String?=null, var password: String?=null, var name: String?=null, var nickname: String?=null)
    private var user = ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        database = Firebase.database.reference

        info = intent.getSerializableExtra("info") as ArrayList<LoginActivity.Info>
        Log.i("info", info.toString())

        mQueue = Volley.newRequestQueue(this)
        //requestInfo()

        readUser()
        id.addTextChangedListener {
            check=10
        }
        checkbutton.setOnClickListener {
            check=0
            for (i in 0 until user.size) {
                if(user[i].user_id.equals(id.text.toString())) {
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
            if(check==0){
                if(able==0){
                    writeNewUser(id.text.toString(),psword.text.toString(),name.text.toString(),nickname.text.toString())
                    finish()
                }
            }
        }
    }
    private fun writeNewUser(user_id:String, password:String, name:String, nickname:String) {
        val newUser= UserViewModel.User(user_id, password, name, nickname)
        database.child("user").child(user_id).setValue(newUser)
    }
    private fun readUser() {

        myRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val getUserInfo=snapshot
                for (u in getUserInfo.children){
                    val user_id: String=u.key.toString()
                    val name: String=u.child("name").value as String
                    val password: String=u.child("password").value as String
                    val nickname: String=u.child("nickname").value as String
                    user.add(User(user_id, password, name, nickname))
                }

            }
        })

    }




}