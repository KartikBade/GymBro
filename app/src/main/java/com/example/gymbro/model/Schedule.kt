package com.example.gymbro.model

class Schedule(
    val name: String,
    val description: String,
    val exercises: ArrayList<Exercise> = ArrayList()
)