package com.zil.binbank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryRecyclerAdapter(val array: List<String>): RecyclerView.Adapter<HistoryRecyclerAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view, parent, false)
        return MyHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.textView?.text = array[position]
    }

    override fun getItemCount(): Int {
        return array.size
    }

    class MyHolder(view: View): RecyclerView.ViewHolder(view){
        var textView: TextView? = null

        init {
            textView = itemView.findViewById(R.id.text_history_item)
        }
    }
}