package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable


class LoginActivity : AppCompatActivity() {
    companion object {
        private const val GOOGLE_TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
        private const val KAKAO_TAG = "kakaoLoginActivity"

        const val BASE_URL = "https://kapi.kakao.com/"
        const val API_KEY = "KakaoAK 82e70293b56bcc9e592b091d1cb39d1a"  // REST API
    }

    val url = "http://192.168.0.105:8080/user"
    private lateinit var mQueue: RequestQueue
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var kakao=0
    private var google=0
    private var normal=0
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    val firebasedatabase = Firebase.database
    val myRef=firebasedatabase.getReference("user")

    data class User(var user_id: String, var password: String, var name: String, var nickname: String)
    private var userlist = ArrayList<User>()

    data class Info(var id: Int, var user_id: String, var password: String, var name: String, var nickname: String):Serializable
    private val info = ArrayList<Info>()

    var usernickname=""
    var userid=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        readUser()
        prefs = this.getSharedPreferences("Prefs", 0)
        editor = prefs.edit()

        Log.i("LoginActivity: kakao", kakao.toString())
        Log.i("google", google.toString())
        Log.i("normal", normal.toString())

        kakao= prefs.getInt("kakao", 0)
        normal= prefs.getInt("normal", 0)
        auth = Firebase.auth
        val user = auth.currentUser
        if(user!=null){
            google=1
        }

        Log.i("LoginActivity: kakao", kakao.toString())
        Log.i("google", google.toString())
        Log.i("normal", normal.toString())

        if(kakao==0 && google==0 && normal==0){
            kakao = intent.getIntExtra("kakao", kakao)
            google = intent.getIntExtra("google", google)
            normal = intent.getIntExtra("normal", normal)
        }
        else{
            //수정 필요
            startMainActivity(kakao,google,normal,"","")
        }

        register_btn.setOnClickListener {
            val intent=Intent(this, RegisterActivity::class.java)
            intent.putExtra("info", info)
            startActivity(intent)
        }

        mQueue = Volley.newRequestQueue(this)
        requestInfo()

        // 로그인 공통 callback 구성
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient=GoogleSignIn.getClient(this,gso)


        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(KAKAO_TAG, "로그인 실패", error)
            }
            else if (token != null) {
                Log.v(KAKAO_TAG, "로그인 성공 ${token.accessToken}")
                kakao+=1
                editor.putInt("kakao", kakao).apply()
                editor.commit()
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
                normal+=1

                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(KAKAO_TAG, "사용자 정보 요청 실패", error)
                    }
                    else if (user != null) {
                        Log.i(KAKAO_TAG, "사용자 정보 요청 성공" +
                                "\n아이디: ${user.id}" +
                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}"
                                )
                        userid = user.id.toString()
                        usernickname = user.kakaoAccount?.profile?.nickname.toString()
                    }
                }
                startMainActivity(kakao,google,normal,userid,usernickname)
            }
        }

        google_login_btn.setOnClickListener {
            signIn()
        }

        kakao_login_btn.setOnClickListener {
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        login_btn.setOnClickListener {
            Log.v("size",userlist.size.toString())
            for (i in 0 until userlist.size) {

                if(userlist[i].user_id.equals(id_txt.text.toString())) {

                    if (userlist[i].password.equals(password_txt.text.toString())) {
                        usernickname=userlist[i].nickname
                        userid=userlist[i].user_id
                        if(checkbox.isChecked){

                            Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
                            normal+=1
                            editor.putInt("normal", normal).apply()
                            editor.commit()
                            Log.i("login normal", normal.toString())
                            startMainActivity(kakao,google,normal,userid,usernickname)
                            break
                        }
                    } else if(i==userlist.size-1) {
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()

                    }
                }
                else if(i==userlist.size-1){
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(GOOGLE_TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(GOOGLE_TAG, "Google sign in failed", e)
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(GOOGLE_TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        google+=1
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(GOOGLE_TAG, "signInWithCredential:failure", task.exception)
                        updateUI(null)
                    }
                }
    }
    // [END auth_with_google]

    // [START signin]
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    //수정 필요
    private fun updateUI(user: FirebaseUser?) {
        startMainActivity(kakao,google,normal,"","")
    }

    private fun startMainActivity(kakao: Int, google: Int, normal: Int, userid:String, usernickname: String) {
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra("kakao", kakao)
        intent.putExtra("google", google)
        intent.putExtra("normal", normal)
        intent.putExtra("id", userid)
        intent.putExtra("nickname", usernickname)
        startActivity(intent)
        finish()
    }

    private fun requestInfo() {
        val request = JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                {
                    //Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                    info.clear()
                    parseJson(it)
                },
                {
                    Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                }
        )
        request.tag = "VolleyRequest"
        mQueue.add(request)
    }
    private fun parseJson(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("id")
            val user_id = item.getString("user_id")
            val password = item.getString("password")
            val name = item.getString("name")
            val nickname = item.getString("nickname")
            info.add(Info(id, user_id, password, name, nickname))
        }
    }
    private fun readUser() {

        myRef.addValueEventListener(object: ValueEventListener {
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
                    Log.v("name",user_id)
                    userlist.add(User(user_id, password, name, nickname))
                }

            }
        })
    }
}