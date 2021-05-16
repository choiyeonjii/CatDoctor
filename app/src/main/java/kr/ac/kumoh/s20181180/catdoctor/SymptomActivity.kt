package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptom)

        classify = intent.getStringArrayListExtra(SymptomClassifyViewModel.CLASSIFY) as ArrayList<String>

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

    // TODO : RecyclerView로 구현했더니, 증상 선택하면 스크롤 후 아래 증상이 미리 선택되어있는 문제 발생
    inner class SymptomAdapter: RecyclerView.Adapter<SymptomAdapter.ViewHolder>() {

        inner class ViewHolder : RecyclerView.ViewHolder,  View.OnClickListener {
            val txText2: TextView

            constructor(root: View) :super(root) {
                root.setOnClickListener(this)
                txText2 = itemView.findViewById<TextView>(R.id.text2)
                txText2.setTag("unselected")
            }
            override fun onClick(v: View?) { //리스트 아이템 클릭 시
                if (txText2.getTag() == "unselected") {
                    txText2.setBackgroundColor(Color.parseColor("#FFE4E1"))
                    txText2.setTag("selected")
                    selectSymptom.add(model.getSymptom(adapterPosition).id)
                }
                else {
                    txText2.setBackgroundColor(Color.WHITE)
                    txText2.setTag("unselected")
                    selectSymptom.remove(model.getSymptom(adapterPosition).id)
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
            holder.txText2.text = model.getSymptom(position).name
        }
    }
}