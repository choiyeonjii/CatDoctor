package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_diagnose.*
import kotlinx.android.synthetic.main.activity_symptom.*
import kr.ac.kumoh.s20181180.catdoctor.DiagnoseViewModel.Companion.DISEASE_ID
import kr.ac.kumoh.s20181180.catdoctor.DiagnoseViewModel.Companion.SYMPTOM_ID
import kr.ac.kumoh.s20181180.catdoctor.MainActivity.Companion.SERVER_URL
import kr.ac.kumoh.s20181180.catdoctor.SymptomViewModel.Companion.SYMPTOM
import org.json.JSONArray
import org.json.JSONObject

class DiagnoseActivity : AppCompatActivity() {
    private var symptom_id = ArrayList<Int>()
    private lateinit var model: DiagnoseViewModel
    private val mAdapter = DiagnoseAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnose)

        symptom_id = intent.getIntegerArrayListExtra(SYMPTOM) as ArrayList<Int>
        Log.i("select_symptom_id", symptom_id.toString())


        lsResultDiagnose.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }

        model = ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory(application))
                .get(DiagnoseViewModel::class.java)

        model.list.observe(this, Observer<ArrayList<DiagnoseViewModel.Disease>> {
            mAdapter.notifyDataSetChanged()
        })

        model.requestDiseaseID(symptom_id)

        backmainBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

    }

    inner class DiagnoseAdapter: RecyclerView.Adapter<DiagnoseActivity.DiagnoseAdapter.ViewHolder>() {
        inner class ViewHolder : RecyclerView.ViewHolder,  View.OnClickListener {
            val txText1: TextView
            val txText2: TextView
            constructor(root: View) :super(root) {
                root.setOnClickListener(this)
                txText1 = itemView.findViewById<TextView>(R.id.diseaseName)
                txText2 = itemView.findViewById<TextView>(R.id.diseaseDefine)
            }
            override fun onClick(v: View?) { //????????? ????????? ?????? ???
                val intent = Intent(this@DiagnoseActivity, DiseaseDetailActivity::class.java)
                intent.putExtra(DISEASE_ID, model.getDisease(adapterPosition).id.toString())
                intent.putExtra(SYMPTOM_ID, symptom_id)
                startActivity(intent)
            }
        }
        override fun getItemCount(): Int {
            return model.getDiseaseSize()
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnoseActivity.DiagnoseAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                    R.layout.item_diagnose,
                    parent,
                    false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: DiagnoseActivity.DiagnoseAdapter.ViewHolder, position: Int) {
            holder.txText1.text = model.getDisease(position).name
            var define = model.getDisease(position).define
            define = define.replace("\n","")
            holder.txText2.text = define
        }
    }
}