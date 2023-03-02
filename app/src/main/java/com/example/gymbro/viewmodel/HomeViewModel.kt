package com.example.gymbro.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gymbro.model.Schedule

class HomeViewModel: ViewModel() {

    var currentSchedule: Schedule? = null
    fun getSchedule(): List<Schedule> {
        return listOf(
            Schedule("Pull, Push, Legs", "Trains full body twice a week with 6 working days."),
            Schedule("Upper, Lower", "Trains full body twice a week with 4 working days."),
            Schedule("Full Body", "Trains full body twice a week with 3 working days."),
            Schedule("Dupple", "Trains upper body twice a week with 7 working days.")
        )
    }
}