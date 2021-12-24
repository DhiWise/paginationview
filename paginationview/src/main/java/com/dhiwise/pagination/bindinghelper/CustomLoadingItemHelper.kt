package com.dhiwise.pagination.bindinghelper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.paginate.recycler.LoadingListItemCreator

/**
 * the abstract class use to bind custom loading layout what you want to load
 * @param layout - is the loading view layout file resource ID,
 * the layout is inflate in recycler view
 */
abstract class CustomLoadingItemHelper(@LayoutRes private var layout: Int) {
    val listItemCreator = object : LoadingListItemCreator{
        override fun onCreateViewHolder(
            parent: ViewGroup?,
            viewType: Int
        ): RecyclerView.ViewHolder {
            val view: View = LayoutInflater.from(parent!!.context)
                .inflate(layout, parent, false)
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            holder?.itemView?.let { onItemBind(it) }
        }

    }

    /**
     * once view holder of custom layout is created and
     * when the view item is bind with recycler view at that time
     * this method is used you can able to modify the view while onItemBind method is called.
     *
     * @param layout is the parent view object which contains the custom layout views.
     */
    abstract fun onItemBind(layout : View)
}