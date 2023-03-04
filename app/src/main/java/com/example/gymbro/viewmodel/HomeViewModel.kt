package com.example.gymbro.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gymbro.model.Schedule

class HomeViewModel: ViewModel() {

    var currentSchedule: Schedule? = null
    private var scheduleList: MutableList<Schedule> = arrayListOf()

    fun addSchedule(schedule: Schedule) {
        scheduleList.add(schedule)
    }

    fun getScheduleList(): List<Schedule> {
        return scheduleList
    }
}