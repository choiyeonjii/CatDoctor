package kr.ac.kumoh.s20181180.catdoctor

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
import kotlinx.android.synthetic.main.activity_diagnose.*

class DiagnoseActivity : AppCompatActivity() {

    private lateinit var model: DiagnoseViewModel
    private val mAdapter = SymptomAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnose)

        lsResult.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }

        model = ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory(application))
                .get(DiagnoseViewModel::class.java)

        model.list1.observe(this, Observer<ArrayList<String>> {
            mAdapter.notifyDataSetChanged()
        })

        model.requestSymptomClassify()
    }

    inner class SymptomAdapter: RecyclerView.Adapter<SymptomAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val txText1: TextView = itemView.findViewById<TextView>(android.R.id.text1)
        }

        override fun getItemCount(): Int {
            return model.getSymptomClassifySize()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                    android.R.layout.simple_list_item_1,
                    parent,
                    false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: SymptomAdapter.ViewHolder, position: Int) {
            holder.txText1.text = model.getSymptomClassify(position)
        }
    }
}