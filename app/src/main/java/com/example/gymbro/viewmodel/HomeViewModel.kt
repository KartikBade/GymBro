package com.example.gymbro.viewmodel

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymbro.activity.HomeActivity
import com.example.gymbro.adapter.ExercisesAdapter
import com.example.gymbro.adapter.MySchedulesAdapter
import com.example.gymbro.databinding.FragmentHomeBinding
import com.example.gymbro.databinding.FragmentScheduleBinding
import com.example.gymbro.model.Exercise
import com.example.gymbro.model.Schedule
import com.example.gymbro.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(
    private val userRepository: UserRepository
): ViewModel() {

    var currentSchedule: Schedule? = null
    var currentExercise: Exercise? = null

    val repCount: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    val weightCount: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    fun signOut() {
        userRepository.signOut()
    }

    fun getFirstName(): String = userRepository.getFirstName()

    fun getFullName(): String = userRepository.getFullName()

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            userRepository.addSchedule(schedule)
        }
    }

    fun bindScheduleAdapterToDatabase(adapter: MySchedulesAdapter, binding: FragmentHomeBinding) {
        viewModelScope.launch {
            userRepository.bindScheduleAdapterToDatabase(adapter, binding)
        }
    }

    fun bindExerciseAdapterToDatabase(adapter: ExercisesAdapter, binding: FragmentScheduleBinding, scheduleName: String) {
        viewModelScope.launch {
            userRepository.bindExerciseAdapterToDatabase(adapter, binding, scheduleName)
        }
    }

    fun addExercise(exercise: Exercise, scheduleName: String) {
        viewModelScope.launch {
            userRepository.addExercise(exercise, scheduleName)
        }
    }

    fun addLog() {
        viewModelScope.launch {
            currentSchedule?.name?.let { schedule ->
                    currentExercise?.name?.let { exercise ->
                        if (repCount.value != null && weightCount.value != null) {
                            userRepository.addLog(schedule, exercise, repCount.value!!, weightCount.value!!)
                        }
                }
            }
        }
    }
}