package kr.ac.kumoh.s20181180.catdoctor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_layout.view.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class ListAdapter(val itemList: ArrayList<ListLayout>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ListAdapter.ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.road.text = itemList[position].road
        holder.phone.text = itemList[position].phone
        var dist = itemList[position].distance.toDouble()
        dist /= 1000
        holder.distance.text = dist.toString()+"km"

        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
        // 전화 이벤트
        holder.itemView.call.setOnClickListener {
            callClickListener.onClick(it, position)
        }
        // 길찾기 이벤트
        holder.itemView.route.setOnClickListener {
            routeClickListener.onClick(it, position)
        }
        //리뷰 이벤트
        holder.itemView.review.setOnClickListener {
            reviewClickListener.onClick(it, position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_list_name)
        val road: TextView = itemView.findViewById(R.id.tv_list_road)
        val phone: TextView = itemView.findViewById(R.id.tv_list_phone)
        val distance: TextView = itemView.findViewById(R.id.tv_list_distance)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener: OnItemClickListener

    // 전화
    interface OnCallClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setCallClickListener(onCallClickListener: OnCallClickListener) {
        this.callClickListener = onCallClickListener
    }

    private lateinit var callClickListener: OnCallClickListener

    // 길찾기
    interface OnRouteClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setRouteClickListener(onRouteClickListener: OnRouteClickListener) {
        this.routeClickListener = onRouteClickListener
    }

    private lateinit var routeClickListener: OnRouteClickListener


    //리뷰
    interface OnReviewClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setReviewClickListener(OnReviewClickListener: OnReviewClickListener) {
        this.reviewClickListener = OnReviewClickListener
    }

    private lateinit var reviewClickListener: OnReviewClickListener
}