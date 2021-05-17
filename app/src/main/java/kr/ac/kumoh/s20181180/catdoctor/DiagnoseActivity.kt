package kr.ac.kumoh.s20181180.catdoctor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_diagnose.*
import kr.ac.kumoh.s20181180.catdoctor.SymptomViewModel.Companion.SYMPTOM

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



        model.requestDisease(symptom_id)
    }

    inner class DiagnoseAdapter: RecyclerView.Adapter<DiagnoseActivity.DiagnoseAdapter.ViewHolder>() {
        inner class ViewHolder : RecyclerView.ViewHolder,  View.OnClickListener {

            val txText2: TextView
            constructor(root: View) :super(root) {
                root.setOnClickListener(this)
                txText2 = itemView.findViewById<TextView>(android.R.id.text1)
                txText2.setTag("unselected")
            }
            override fun onClick(v: View?) { //리스트 아이템 클릭 시

            }
        }
        override fun getItemCount(): Int {
            return model.getDiseaseSize()
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnoseActivity.DiagnoseAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                    android.R.layout.simple_list_item_1,
                    parent,
                    false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: DiagnoseActivity.DiagnoseAdapter.ViewHolder, position: Int) {
            holder.txText2.text = model.getDisease(position).name
        }
    }
}