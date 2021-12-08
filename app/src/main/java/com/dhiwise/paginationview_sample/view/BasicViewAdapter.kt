package com.dhiwise.paginationview_sample.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

public class BasicViewAdapter<T>(
    val layoutId: Int,
    var list: List<T>,
    val onBind: (item: T, vh: BasicViewHolder) -> Unit
) : RecyclerView.Adapter<BasicViewAdapter.BasicViewHolder>() {

    public fun updateData(newData: List<T>): Unit {
        list = newData
        notifyDataSetChanged()
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return BasicViewHolder(view)
    }

    public override fun onBindViewHolder(holder: BasicViewHolder, position: Int) {
        val item = list[position]
        onBind(item, holder)
    }

    public override fun getItemCount(): Int = list.size

    class BasicViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
