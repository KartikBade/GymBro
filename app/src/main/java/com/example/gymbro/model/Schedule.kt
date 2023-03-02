package com.example.gymbro.model

class Schedule(
    val name: String,
    val instructions: String,
    val exercises: ArrayList<Exercise> = ArrayList()
)