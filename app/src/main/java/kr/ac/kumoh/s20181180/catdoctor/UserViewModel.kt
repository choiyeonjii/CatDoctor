package kr.ac.kumoh.s20181180.catdoctor

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlin.collections.ArrayList

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var database: DatabaseReference

    data class User(var user_id: String, var password: String, var name: String, var nickname: String)
    val list = MutableLiveData<ArrayList<UserViewModel.User>>()
    private val user = ArrayList<UserViewModel.User>()

init {
    list.value = user
    database = Firebase.database.reference
}
    private fun writeNewUser(id:String, user_id:String, password:String, name:String, nickname:String) {
        val newUser=User(user_id,password,name,nickname)
        user.add(newUser)
        database.child("user").child(id).child("user_id").setValue(newUser)

    }

}