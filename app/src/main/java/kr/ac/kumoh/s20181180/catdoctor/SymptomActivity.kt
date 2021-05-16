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
import kotlinx.android.synthetic.main.activity_symptom.*

class SymptomActivity : AppCompatActivity() {

    private lateinit var model: SymptomViewModel
    private val mAdapter = SymptomAdapter()
    private lateinit var classify:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptom)

        classify = intent.getStringExtra(SymptomClassifyViewModel.CLASSIFY).toString()

        lsResultSymptom.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }

        model = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory(application))
            .get(SymptomViewModel::class.java)

        model.list.observe(this, Observer<ArrayList<SymptomViewModel.Symptom>> {
            mAdapter.notifyDataSetChanged()
        })

        model.requestSymptom(classify)
    }

    inner class SymptomAdapter: RecyclerView.Adapter<SymptomAdapter.ViewHolder>() {

        inner class ViewHolder : RecyclerView.ViewHolder,  View.OnClickListener {
            val txText1: TextView

            constructor(root: View) :super(root) {
                root.setOnClickListener(this)
                txText1 = itemView.findViewById<TextView>(android.R.id.text1)
            }
            override fun onClick(v: View?) { //리스트 아이템 클릭 시
//                val intent = Intent(this@SymptomActivity, SymptomActivity::class.java)
//                intent.putExtra(DiagnoseViewModel.CLASSIFY, model.getSymptomClassify(adapterPosition))
//                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return model.getSymptomSize()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                android.R.layout.simple_list_item_1,
                parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: SymptomAdapter.ViewHolder, position: Int) {
            holder.txText1.text = model.getSymptom(position).name
            Log.i("symptom111", model.getSymptom(position).name )
        }
    }
}