package com.example.gymbro.model

data class Schedule(
    val name: String,
    val description: String,
    val exercises: ArrayList<Exercise> = ArrayList()
)