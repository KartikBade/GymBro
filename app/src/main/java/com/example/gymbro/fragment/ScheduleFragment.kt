package com.example.gymbro.fragment

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gymbro.R
import com.example.gymbro.activity.HomeActivity
import com.example.gymbro.adapter.ExercisesAdapter
import com.example.gymbro.databinding.FragmentScheduleBinding
import com.example.gymbro.model.Exercise
import com.example.gymbro.viewmodel.HomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ScheduleFragment : Fragment() {

    private lateinit var binding: FragmentScheduleBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var customAlertDialogView: View
    private lateinit var materialScheduleAlertDialog: MaterialAlertDialogBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(inflater)
        homeViewModel = (activity as HomeActivity).homeViewModel
        materialScheduleAlertDialog = MaterialAlertDialogBuilder(binding.root.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
        val exerciseAdapter = ExercisesAdapter {
            homeViewModel.currentExercise = it
            findNavController().navigate(R.id.action_scheduleFragment_to_exerciseFragment)
        }
        binding.rvExercise.adapter = exerciseAdapter
        homeViewModel.currentSchedule?.name?.let {
            homeViewModel.bindExerciseAdapterToDatabase(exerciseAdapter, binding, it)
        }
        binding.fabAddExercise.setOnClickListener {
            customAlertDialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_add_exercise, binding.root, false)
            launchCustomAlertDialog()
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_scheduleFragment_to_homeFragment)
        }
        binding.tvScheduleName.text = homeViewModel.currentSchedule?.name.toString()
    }

    private fun launchCustomAlertDialog() {
        val tvExerciseName: TextView = customAlertDialogView.findViewById(R.id.et_exercise_name)
        val tvExerciseDescription: TextView = customAlertDialogView.findViewById(R.id.et_exercise_description)

        materialScheduleAlertDialog.setView(customAlertDialogView)
            .setTitle("Add Exercise")
            .setPositiveButton("Add") { dialog, _ ->
                val exerciseName = tvExerciseName.text.toString()
                val exerciseDescription = tvExerciseDescription.text.toString()

                if (exerciseName.isEmpty()) {
                    Toast.makeText(context, "Failed: Add Name", Toast.LENGTH_LONG).show()
                } else if (exerciseDescription.isEmpty()) {
                    Toast.makeText(context, "Failed: Add Description", Toast.LENGTH_LONG).show()
                } else {
                    homeViewModel.currentSchedule?.name?.let {
                        homeViewModel.addExercise(Exercise(exerciseName, exerciseDescription), it)
                    }
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}