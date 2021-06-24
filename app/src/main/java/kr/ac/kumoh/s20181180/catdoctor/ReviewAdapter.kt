package kr.ac.kumoh.s20181180.catdoctor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewAdapter(val review:ArrayList<HospitalReviewActivity.Review>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val star: TextView = itemView.findViewById(R.id.hospital_review_star)
        val nickname: TextView = itemView.findViewById(R.id.hospital_review_user)
        val date: TextView = itemView.findViewById(R.id.hospital_review_date)
        val title: TextView = itemView.findViewById(R.id.hospital_review_title)
        val content: TextView = itemView.findViewById(R.id.hospital_review_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_review,parent,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return review.size
    }

    //data class Review(var nickname: String, var star: String, var title: String, var content: String, var date: String)
    override fun onBindViewHolder(holder: ReviewAdapter.ViewHolder, position: Int) {
        var star_string=review[position].star.toString()
        var star_int=star_string.toInt()
        var showString=""
        for(i in 1..star_int){
            showString+="💛 "
        }
        for(i in star_int+1..5){
            showString+="🤍 "
        }
        showString+=star_string
        holder.star.text = showString
        holder.nickname.text = review[position].nickname
        holder.title.text = review[position].title
        holder.content.text = review[position].content
        holder.date.text = review[position].date


    }



}
