package com.weather.info.ui.dashboard.ui.hisory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weather.info.base.adapter.BaseAdapter
import com.weather.info.data.room.entity.History
import com.weather.info.databinding.ItemHistoryBinding

class HistoryAdapter(val listener: HistoryListener) :
    BaseAdapter<History, HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(dataList[position])
    }

    inner class ViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.itemLayout.setOnClickListener {
                listener.onItemClick(it, absoluteAdapterPosition, dataList[absoluteAdapterPosition])
            }

            binding.ivDelete.setOnClickListener {
                listener.onDeleteHistoryItem(
                    it,
                    absoluteAdapterPosition,
                    dataList[absoluteAdapterPosition]
                )
            }

        }

        fun onBind(history: History) {
            binding.history = history
            binding.executePendingBindings()
        }
    }

    interface HistoryListener {
        fun onItemClick(view: View, position: Int, history: History)
        fun onDeleteHistoryItem(view: View, position: Int, history: History)
    }

}