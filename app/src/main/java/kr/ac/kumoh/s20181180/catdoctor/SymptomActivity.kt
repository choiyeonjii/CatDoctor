package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_symptom.*
import kotlinx.android.synthetic.main.activity_symptomclassify.*
import kr.ac.kumoh.s20181180.catdoctor.SymptomViewModel.Companion.SYMPTOM

class SymptomActivity : AppCompatActivity() {

    private lateinit var model: SymptomViewModel
    private val mAdapter = SymptomAdapter()
    private var classify = ArrayList<String>()
    var selectSymptom = ArrayList<Int>()
    lateinit var selectItems : SparseBooleanArray
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptom)

        classify = intent.getStringArrayListExtra(SymptomClassifyViewModel.CLASSIFY) as ArrayList<String>
        selectItems = SparseBooleanArray(0)

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

        symptomTextView.setText("'" + classify[count] + "'의 증상을 선택해주세요.")
        model.requestSymptom(classify[count++])
        symBtn.setOnClickListener {
            if (count < classify.size) {
                selectItems = SparseBooleanArray(0)
                model = ViewModelProvider(this,
                        ViewModelProvider.AndroidViewModelFactory(application))
                        .get(SymptomViewModel::class.java)
                model.list.observe(this, Observer<ArrayList<SymptomViewModel.Symptom>> {
                    mAdapter.notifyDataSetChanged()
                })
                symptomTextView.setText("'" + classify[count] + "'의 증상을 선택해주세요.")
                model.requestSymptom(classify[count++])
            }
            else {  // 선택한 카테고리 모두 끝났을때
                val intent = Intent(this, DiagnoseActivity::class.java)
                intent.putExtra(SYMPTOM, selectSymptom)
                startActivity(intent)
            }
        }
    }
    inner class SymptomAdapter: RecyclerView.Adapter<SymptomAdapter.ViewHolder>() {
        inner class ViewHolder : RecyclerView.ViewHolder,  View.OnClickListener {
            val txText1: TextView
            constructor(root: View) :super(root) {
                root.setOnClickListener(this)
                txText1 = itemView.findViewById<TextView>(R.id.text2)
            }
            override fun onClick(v: View?) { //리스트 아이템 클릭 시
                if (selectItems.get(adapterPosition, false)) {
                    txText1.setBackgroundColor(Color.WHITE)
                    selectItems.put(adapterPosition, false)
                    selectSymptom.remove(model.getSymptom(adapterPosition).id)
                }
                else {
                    txText1.setBackgroundColor(Color.parseColor("#FFE4E1"))
                    selectItems.put(adapterPosition, true)
                    selectSymptom.add(model.getSymptom(adapterPosition).id)
                }
            }
        }
        override fun getItemCount(): Int {
            return model.getSymptomSize()
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                    R.layout.item_symptom,
                    parent,
                    false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: SymptomAdapter.ViewHolder, position: Int) {
            holder.txText1.text = model.getSymptom(position).name
            if (selectItems.get(position, false))
                holder.txText1.setBackgroundColor(Color.parseColor("#FFE4E1"))
            else
                holder.txText1.setBackgroundColor(Color.WHITE)
        }
    }
}