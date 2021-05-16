package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kotlinx.android.synthetic.main.activity_symptomclassify.*
import kotlinx.android.synthetic.main.item_symptomclassify.*
import kr.ac.kumoh.s20181180.catdoctor.SymptomClassifyViewModel.Companion.CLASSIFY

class SymptomClassifyActivity : AppCompatActivity() {

    private lateinit var model: SymptomClassifyViewModel
    private val mAdapter = SymptomClassifyAdapter()
    private val selectClassify = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptomclassify)

        lsResult.apply {
            layoutManager = GridLayoutManager(applicationContext, 3)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }

        model = ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory(application))
                .get(SymptomClassifyViewModel::class.java)

        model.list1.observe(this, Observer<ArrayList<SymptomClassifyViewModel.SymptomClassify>> {
            mAdapter.notifyDataSetChanged()
        })

        model.requestSymptomClassify()

        symclassifyBtn.setOnClickListener {
            if (selectClassify.size != 0) {
                val intent = Intent(this, SymptomActivity::class.java)
                Log.i("selectClassify", selectClassify.toString())
                intent.putExtra(CLASSIFY, selectClassify)
                startActivity(intent)
            }
            else
                Toast.makeText(getApplication(), "카테고리를 선택해주세요.", Toast.LENGTH_LONG).show()
        }
    }

    inner class SymptomClassifyAdapter: RecyclerView.Adapter<SymptomClassifyAdapter.ViewHolder>() {

        inner class ViewHolder : RecyclerView.ViewHolder,  View.OnClickListener {
            val txText1: TextView
            val image: NetworkImageView
            val linear: LinearLayout

            constructor(root: View) :super(root) {
                root.setOnClickListener(this)
                txText1 = itemView.findViewById<TextView>(R.id.text1)
                image = itemView.findViewById<NetworkImageView>(R.id.image)
                linear = itemView.findViewById<LinearLayout>(R.id.linear)
                txText1.setTag("unselected")
            }
            override fun onClick(v: View?) { //리스트 아이템 클릭 시
                if (txText1.getTag() == "unselected") {
                    linear.setBackgroundColor(Color.parseColor("#FFE4E1"))
                    txText1.setTag("selected")
                    selectClassify.add(model.getSymptomClassify(adapterPosition).classify)
                }
                else {
                    linear.setBackgroundColor(Color.WHITE)
                    txText1.setTag("unselected")
                    selectClassify.remove(model.getSymptomClassify(adapterPosition).classify)
                }
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
            holder.txText1.text = model.getSymptomClassify(position).classify
            holder.image.setImageUrl(model.getImageUrl(position), model.imageLoader)
        }
    }
}