package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_symptomclassify.*
import kr.ac.kumoh.s20181180.catdoctor.SymptomClassifyViewModel.Companion.CLASSIFY

class SymptomClassifyActivity : AppCompatActivity() {

    private lateinit var model: SymptomClassifyViewModel
    private val mAdapter = SymptomClassifyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptomclassify)

        lsResult.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }

        model = ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory(application))
                .get(SymptomClassifyViewModel::class.java)

        model.list.observe(this, Observer<ArrayList<String>> {
            mAdapter.notifyDataSetChanged()
        })

        model.requestSymptomClassify()

        symclassifyBtn.setOnClickListener {
            val intent = Intent(this@SymptomClassifyActivity, SymptomActivity::class.java)
            startActivity(intent)
        }
    }

    inner class SymptomClassifyAdapter: RecyclerView.Adapter<SymptomClassifyAdapter.ViewHolder>() {

        inner class ViewHolder : RecyclerView.ViewHolder,  View.OnClickListener {
                    val txText1: TextView

            constructor(root: View) :super(root) {
                root.setOnClickListener(this)
                txText1 = itemView.findViewById<TextView>(R.id.text1)
            }
            override fun onClick(v: View?) { //리스트 아이템 클릭 시

//                val intent = Intent(this@SymptomClassifyActivity, SymptomActivity::class.java)
//                intent.putExtra(CLASSIFY, model.getSymptomClassify(adapterPosition))
//                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return model.getSymptomClassifySize()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomClassifyAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                    R.layout.item_symptomclassify,
                    parent,
                    false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: SymptomClassifyAdapter.ViewHolder, position: Int) {
            holder.txText1.text = model.getSymptomClassify(position)
        }
    }
}