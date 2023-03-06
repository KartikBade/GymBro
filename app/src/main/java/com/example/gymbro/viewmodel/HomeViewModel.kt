package com.example.gymbro.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.gymbro.model.Schedule
import com.example.gymbro.repository.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository
): ViewModel() {

    var currentSchedule: Schedule? = null
    private var scheduleList: MutableList<Schedule> = arrayListOf()

    fun addSchedule(schedule: Schedule) {
        scheduleList.add(schedule)
    }

    fun getScheduleList(): List<Schedule> {
        return scheduleList
    }

    fun signOut() {
        userRepository.signOut()
    }

    fun getFirstName(): String = userRepository.getFirstName()
}