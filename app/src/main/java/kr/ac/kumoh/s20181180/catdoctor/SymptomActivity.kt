package kr.ac.kumoh.s20181180.catdoctor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_symptom.*

class SymptomActivity : AppCompatActivity() {

    private lateinit var classify:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptom)

        classify = intent.getStringExtra(DiagnoseViewModel.CLASSIFY).toString()
        test.text = classify
    }
}