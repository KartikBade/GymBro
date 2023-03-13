package com.example.gymbro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbro.databinding.ListItemExercisesBinding
import com.example.gymbro.model.Exercise
import com.example.gymbro.model.Schedule

class ExercisesAdapter(val listener: (Exercise) -> Unit): ListAdapter<Exercise, ExercisesAdapter.ExercisesAdapterViewHolder>(DiffCallBack) {

    class ExercisesAdapterViewHolder(val binding: ListItemExercisesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: Exercise) {
            binding.tvExerciseName.text = exercise.name
            binding.tvExerciseInstructions.text = exercise.instructions
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercisesAdapterViewHolder {
        return ExercisesAdapterViewHolder(ListItemExercisesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ExercisesAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.cvExercise.setOnClickListener {
            listener(getItem(position))
        }
    }

    companion object DiffCallBack: DiffUtil.ItemCallback<Exercise>() {
        override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem.instructions == newItem.instructions
        }
    }
}