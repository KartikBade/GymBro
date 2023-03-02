package com.example.gymbro.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymbro.R
import com.example.gymbro.adapter.MySchedulesAdapter
import com.example.gymbro.databinding.FragmentHomeBinding
import com.example.gymbro.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mySchedulesAdapter = MySchedulesAdapter()
        binding.rvSchedule.adapter = mySchedulesAdapter
        mySchedulesAdapter.submitList(homeViewModel.getSchedule())

        if (mySchedulesAdapter.itemCount == 0) {
            binding.emptyRvScheduleAddButton.visibility = View.VISIBLE
            binding.emptyRvScheduleTextView.visibility = View.VISIBLE
        }
        if (binding.rvFriends.isEmpty()) {
            binding.emptyRvFriendsTextView.visibility = View.VISIBLE
        }
    }
}