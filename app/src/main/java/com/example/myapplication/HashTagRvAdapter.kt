package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HashTagRvAdapter(val dataset: MutableList<String> ,val context : Context) : RecyclerView.Adapter<HashTagRvAdapter.MyVH>(){

    private lateinit var mListner : MyOnItemClickListner
    interface MyOnItemClickListner {
        fun onItemClick(position: Int)
    }

    fun msetupOnClickListner(listner : MyOnItemClickListner){
        mListner = listner
    }

    inner class MyVH(itemView : View, listner : MyOnItemClickListner) : RecyclerView.ViewHolder(itemView) {
        val hashtag : TextView = itemView.findViewById(R.id.hashtag_item_text)
        val deleteButton : ImageView = itemView.findViewById(R.id.deleteHashtagButton)

        init {
            deleteButton.setOnClickListener {
                listner.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hashtag_rv_item , parent , false)
        return MyVH(view , mListner)
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        holder.hashtag.text = dataset[position]
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}