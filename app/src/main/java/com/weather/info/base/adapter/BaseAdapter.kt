package com.weather.info.base.adapter

import androidx.recyclerview.widget.RecyclerView

/**
 *  This is a Base adapter
 *  using this adapter you can set any recycler view easily
 *  Benefits :-
 *   - Removing dependency of list inside fragment
 *   - Removing call notify when list update
 *   - Easy to add items using methods :- add(item), add(position, item), addAll(list of items)
 *   - Easy to update any item update(position, item)
 *   - Easy to remove item remove(position)
 *   - Easy to clear list clear()
 *  Try to change in yourself because World is changing
 * */
abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected var dataList: ArrayList<T> = ArrayList()

    fun clear() {
        val size: Int = dataList.size
        for (i in 0 until size) {
            remove(0)
        }
    }

    fun addAll(fileModels: List<T>) {
        for (fileModel in fileModels) {
            add(fileModel)
        }
    }

    fun add(fileModel: T) {
        dataList.add(fileModel)
        notifyItemInserted(dataList.size - 1)
    }

    fun add(position: Int, fileModel: T) {
        dataList.add(position, fileModel)
        notifyItemInserted(position)
    }

    fun update(position: Int, fileModel: T) {
        if (dataList.isNotEmpty() && dataList.size > position) {
            dataList[position] = fileModel
            notifyItemChanged(position)
        }
    }

    fun remove(adapterPosition: Int) {
        dataList.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
        notifyItemRangeChanged(adapterPosition, dataList.size)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}