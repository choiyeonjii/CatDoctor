package kr.ac.kumoh.s20181180.catdoctor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kotlinx.android.synthetic.main.activity_symptomclassify.*
import kr.ac.kumoh.s20181180.catdoctor.DiagnoseViewModel.Companion.CLASSIFY

class SymptomClassifyActivity : AppCompatActivity() {

    private lateinit var model: DiagnoseViewModel
    private val mAdapter = SymptomAdapter()

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
                .get(DiagnoseViewModel::class.java)

        model.list1.observe(this, Observer<ArrayList<DiagnoseViewModel.SymptomClassify>> {
            mAdapter.notifyDataSetChanged()
        })

        model.requestSymptomClassify()
    }

    inner class SymptomAdapter: RecyclerView.Adapter<SymptomAdapter.ViewHolder>() {

        inner class ViewHolder : RecyclerView.ViewHolder,  View.OnClickListener {
            val txText1: TextView
            val image: NetworkImageView

            constructor(root: View) :super(root) {
                root.setOnClickListener(this)
                txText1 = itemView.findViewById<TextView>(R.id.text1)
                image = itemView.findViewById<NetworkImageView>(R.id.image)
            }
            override fun onClick(v: View?) { //리스트 아이템 클릭 시
                val intent = Intent(this@SymptomClassifyActivity, SymptomActivity::class.java)
                intent.putExtra(CLASSIFY, model.getSymptomClassify(adapterPosition).classify)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return model.getSymptomClassifySize()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                    R.layout.item_symptomclassify,
                    parent,
                    false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: SymptomAdapter.ViewHolder, position: Int) {
            holder.txText1.text = model.getSymptomClassify(position).classify
            holder.image.setImageUrl(model.getImageUrl(position), model.imageLoader)
        }
    }
}