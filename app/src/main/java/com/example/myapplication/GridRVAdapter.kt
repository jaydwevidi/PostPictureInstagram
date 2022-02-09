package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GridRVAdapter(val dataset: MutableList<ImageFileObject>, val context : Context) : RecyclerView.Adapter<GridRVAdapter.MyVH>(){

    private lateinit var mListner : MyOnItemClickListner
    interface MyOnItemClickListner {
        fun onItemClick(position: Int)
    }

    fun msetupOnClickListner(listner : MyOnItemClickListner){
        mListner = listner
    }

    inner class MyVH(itemView : View , listner : MyOnItemClickListner) : RecyclerView.ViewHolder(itemView) {
        val imageView : ImageView = itemView.findViewById(R.id.single_image_1)

        init {
            itemView.setOnClickListener {
                listner.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_image , parent , false)
        return MyVH(view , mListner)
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        Glide.with(context)
            .load(dataset[position].file)
            .centerCrop()
            .into(holder.imageView)


    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}