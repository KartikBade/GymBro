package com.example.gymbro.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymbro.R
import com.example.gymbro.activity.HomeActivity
import com.example.gymbro.adapter.MySchedulesAdapter
import com.example.gymbro.databinding.AddScheduleAlertDialogBinding
import com.example.gymbro.databinding.FragmentHomeBinding
import com.example.gymbro.model.Schedule
import com.example.gymbro.viewmodel.HomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var materialScheduleAlertDialog: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        homeViewModel = (activity as HomeActivity).homeViewModel
        materialScheduleAlertDialog = MaterialAlertDialogBuilder(binding.root.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mySchedulesAdapter = MySchedulesAdapter {
            homeViewModel.currentSchedule = it
            findNavController().navigate(R.id.action_homeFragment_to_scheduleFragment)
        }
        binding.rvSchedule.adapter = mySchedulesAdapter
        mySchedulesAdapter.submitList(homeViewModel.getScheduleList())

        if (mySchedulesAdapter.itemCount == 0) {
            binding.emptyRvScheduleAddButton.visibility = View.VISIBLE
            binding.emptyRvScheduleTextView.visibility = View.VISIBLE
        }
        if (binding.rvFriends.isEmpty()) {
            binding.emptyRvFriendsTextView.visibility = View.VISIBLE
        }
        binding.btnAddSchedule.setOnClickListener {
            customAlertDialogView = LayoutInflater.from(context).inflate(R.layout.add_schedule_alert_dialog, binding.root, false)
            launchCustomAlertDialog()
        }
        binding.emptyRvScheduleAddButton.setOnClickListener {
            customAlertDialogView = LayoutInflater.from(context).inflate(R.layout.add_schedule_alert_dialog, binding.root, false)
            launchCustomAlertDialog()
        }
    }

    private fun launchCustomAlertDialog() {
        val tvScheduleName: TextView = customAlertDialogView.findViewById(R.id.et_schedule_name)
        val tvScheduleDescription: TextView = customAlertDialogView.findViewById(R.id.et_schedule_description)

        materialScheduleAlertDialog.setView(customAlertDialogView)
            .setTitle("Add Schedule")
            .setPositiveButton("Add") { dialog, _ ->
                val scheduleName = tvScheduleName.text.toString()
                val scheduleDescription = tvScheduleDescription.text.toString()
                homeViewModel.addSchedule(Schedule(scheduleName, scheduleDescription))

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}