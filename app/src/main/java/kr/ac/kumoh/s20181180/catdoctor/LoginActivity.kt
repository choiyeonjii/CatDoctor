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
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable


class LoginActivity : AppCompatActivity() {
    val url = "http://192.168.200.104:8080/user"
    private lateinit var mQueue: RequestQueue
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var kakao=0
    private var google=0
    private var normal=0
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    data class Info(var id: Int, var user_id: String, var password: String, var name: String, var nickname: String):Serializable
    private val info = ArrayList<Info>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
            startMainActivity(kakao,google,normal)
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
                startMainActivity(kakao,google,normal)
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
            for (i in 0 until info.size) {
                if(info[i].user_id.equals(id_txt.text.toString())) {
                    if (info[i].password.equals(password_txt.text.toString())) {
                        if(checkbox.isChecked){
                            Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
                            normal+=1
                            editor.putInt("normal", normal).apply()
                            editor.commit()
                            Log.i("login normal", normal.toString())
                            startMainActivity(kakao,google,normal)
                            break
                        }
                        else{
                            Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
                            normal+=1
                            startMainActivity(kakao,google,normal)
                            break
                        }
                    } else {
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
                        Log.e("LOGIN FAIL", "로그인 실패")
                    }
                }
                else{
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
                    Log.e("LOGIN FAIL", "로그인 실패")
                }
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
//    }

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

    private fun updateUI(user: FirebaseUser?) {
        startMainActivity(kakao,google,normal)
    }

    companion object {
        private const val GOOGLE_TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
        private const val KAKAO_TAG = "kakaoLoginActivity"
    }

    private fun startMainActivity(kakao: Int, google: Int, normal: Int) {
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra("kakao", kakao)
        intent.putExtra("google", google)
        intent.putExtra("normal", normal)
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
}