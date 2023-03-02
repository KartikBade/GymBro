package com.example.gymbro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbro.R
import com.example.gymbro.databinding.MySchedulesListItemBinding
import com.example.gymbro.model.Schedule

class MySchedulesAdapter: ListAdapter<Schedule, MySchedulesAdapter.MyScheduleViewHolder>(DiffCallBack) {

    class MyScheduleViewHolder(val binding: MySchedulesListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: Schedule) {
            binding.tvScheduleName.text = schedule.name
            binding.tvScheduleDescription.text = schedule.instructions
            binding.scheduleImage.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyScheduleViewHolder {
        return MyScheduleViewHolder(MySchedulesListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
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