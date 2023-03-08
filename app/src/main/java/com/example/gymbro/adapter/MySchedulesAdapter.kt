package com.example.gymbro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbro.databinding.ListItemMySchedulesBinding
import com.example.gymbro.model.Schedule

class MySchedulesAdapter(val listener: (Schedule) -> Unit): ListAdapter<Schedule, MySchedulesAdapter.MyScheduleViewHolder>(DiffCallBack) {

    class MyScheduleViewHolder(val binding: ListItemMySchedulesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: Schedule) {
            binding.tvScheduleName.text = schedule.name
            binding.tvScheduleDescription.text = schedule.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyScheduleViewHolder {
        return MyScheduleViewHolder(ListItemMySchedulesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyScheduleViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
        holder.binding.parentConstraintLayout.setOnClickListener {
            listener(currentItem)
        }
    }

    companion object DiffCallBack: DiffUtil.ItemCallback<Schedule>() {
        override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem.name == newItem.name
        }

    }
}