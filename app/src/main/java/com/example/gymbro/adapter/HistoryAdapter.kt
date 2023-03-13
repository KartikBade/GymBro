package com.example.gymbro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbro.databinding.ListItemHistoryLogsBinding
import com.example.gymbro.model.History

class HistoryAdapter: ListAdapter<History, HistoryAdapter.HistoryAdapterViewHolder>(DiffCallBack) {

    class HistoryAdapterViewHolder(val binding: ListItemHistoryLogsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            binding.tvTime.text = history.time
            binding.tvReps.text = history.reps.toString()
            binding.tvWeight.text = history.weight.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapterViewHolder {
        return HistoryAdapterViewHolder(ListItemHistoryLogsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HistoryAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallBack: DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.time == newItem.time && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.reps == newItem.reps && oldItem.weight == newItem.weight
        }

    }
}