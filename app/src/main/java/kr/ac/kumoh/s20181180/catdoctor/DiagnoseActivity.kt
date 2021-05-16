package kr.ac.kumoh.s20181180.catdoctor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_diagnose.*
import kr.ac.kumoh.s20181180.catdoctor.SymptomViewModel.Companion.SYMPTOM

class DiagnoseActivity : AppCompatActivity() {
    private var symptom = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnose)

        symptom = intent.getIntegerArrayListExtra(SYMPTOM) as ArrayList<Int>

        for (i in 0 until symptom.size) {
            Log.i("select symptom id", symptom[i].toString())
        }

    }
}