package com.example.gymbro.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.gymbro.R
import com.example.gymbro.databinding.ActivityAuthBinding
import com.example.gymbro.repository.UserRepository
import com.example.gymbro.viewmodel.AuthViewModel
import com.example.gymbro.viewmodel.AuthViewModelProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var navController: NavController
    private lateinit var userRepository: UserRepository
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseRef: FirebaseFirestore
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.authFragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        mAuth = FirebaseAuth.getInstance()
        mDatabaseRef = Firebase.firestore
        userRepository = UserRepository(this, mAuth, mDatabaseRef)

        val authViewModelProviderFactory = AuthViewModelProviderFactory(userRepository)
        authViewModel = ViewModelProvider(this, authViewModelProviderFactory).get(AuthViewModel::class.java)
    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }
}