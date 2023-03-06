package com.example.gymbro.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gymbro.R
import com.example.gymbro.activity.HomeActivity
import com.example.gymbro.adapter.MySchedulesAdapter
import com.example.gymbro.databinding.FragmentHomeBinding
import com.example.gymbro.model.Schedule
import com.example.gymbro.viewmodel.HomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var materialScheduleAlertDialog: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView: View
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseRef: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        mAuth = FirebaseAuth.getInstance()
        mDatabaseRef = Firebase.firestore
        homeViewModel = (activity as HomeActivity).homeViewModel
        materialScheduleAlertDialog = MaterialAlertDialogBuilder(binding.root.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun launchCustomAlertDialog() {
        val tvScheduleName: TextView = customAlertDialogView.findViewById(R.id.et_schedule_name)
        val tvScheduleDescription: TextView = customAlertDialogView.findViewById(R.id.et_schedule_description)

        materialScheduleAlertDialog.setView(customAlertDialogView)
            .setTitle("Add Schedule")
            .setPositiveButton("Add") { dialog, _ ->
                val scheduleName = tvScheduleName.text.toString()
                val scheduleDescription = tvScheduleDescription.text.toString()

                if (scheduleName.isEmpty()) {
                    Toast.makeText(context, "Failed: Add Name", Toast.LENGTH_LONG).show()
                } else if (scheduleDescription.isEmpty()) {
                    Toast.makeText(context, "Failed: Add Description", Toast.LENGTH_LONG).show()
                } else {
                    homeViewModel.addSchedule(Schedule(scheduleName, scheduleDescription))
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupUi() {
        val mySchedulesAdapter = MySchedulesAdapter {
            homeViewModel.currentSchedule = it
            findNavController().navigate(R.id.action_homeFragment_to_scheduleFragment)
        }
        binding.rvSchedule.adapter = mySchedulesAdapter
        homeViewModel.bindAdapterToDatabase(mySchedulesAdapter, binding)

        val firstName = homeViewModel.getFirstName()
        binding.tvGreetingName.text = getString(R.string.greeting_name, firstName)

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
}