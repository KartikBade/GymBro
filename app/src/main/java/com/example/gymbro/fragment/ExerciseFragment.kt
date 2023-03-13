package com.example.gymbro.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.gymbro.R
import com.example.gymbro.activity.HomeActivity
import com.example.gymbro.adapter.HistoryAdapter
import com.example.gymbro.databinding.FragmentExerciseBinding
import com.example.gymbro.viewmodel.HomeViewModel

class ExerciseFragment : Fragment() {

    private lateinit var binding: FragmentExerciseBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseBinding.inflate(inflater)
        homeViewModel = (activity as HomeActivity).homeViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
        binding.tvExerciseTitle.text = homeViewModel.currentExercise?.name.toString()
        binding.btnBackToSchedule.setOnClickListener {
            findNavController().navigate(R.id.action_exerciseFragment_to_scheduleFragment)
        }

        homeViewModel.repCount.observe(requireActivity()) {
            binding.tvRepCount.text = it.toString()
        }
        homeViewModel.weightCount.observe(requireActivity()) {
            binding.tvWeightCount.text = it.toString()
        }

        binding.ivDecreaseReps.setOnClickListener {
            if (homeViewModel.repCount.value!! > 0) {
                homeViewModel.repCount.value = homeViewModel.repCount.value?.dec()
            }
        }
        binding.ivIncreaseReps.setOnClickListener {
            homeViewModel.repCount.value = homeViewModel.repCount.value?.inc()
        }
        binding.ivDecreaseWeight.setOnClickListener {
            if (homeViewModel.weightCount.value!! > 0) {
                homeViewModel.weightCount.value = homeViewModel.weightCount.value?.dec()
            }
        }
        binding.ivIncreaseWeight.setOnClickListener {
            homeViewModel.weightCount.value = homeViewModel.weightCount.value?.inc()
        }
        binding.btnSetComplete.setOnClickListener {
            homeViewModel.addLog()
        }
        val historyAdapter = HistoryAdapter()
        binding.rvLogs.adapter = historyAdapter
        homeViewModel.bindHistoryAdapterToDatabase(historyAdapter, binding)
    }
}