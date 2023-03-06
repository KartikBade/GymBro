package com.example.gymbro.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.gymbro.repository.UserRepository

class AuthViewModel(
    private val userRepository: UserRepository
): ViewModel() {

    fun setupAccount(requestCode: Int, data: Intent?) {
        userRepository.setupAccount(requestCode, data)
    }
}