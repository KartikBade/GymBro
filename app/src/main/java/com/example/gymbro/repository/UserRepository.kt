package com.example.gymbro.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.gymbro.activity.HomeActivity
import com.example.gymbro.fragment.REQUEST_CODE_SIGN_IN
import com.example.gymbro.model.Schedule
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(
    private val context: Context,
    private val userAuthRef: FirebaseAuth,
    private val userDatabaseRef: FirebaseFirestore
) {

    fun setupAccount(requestCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                val credentials = GoogleAuthProvider.getCredential(it.idToken, null)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        userAuthRef.signInWithCredential(credentials).await()
                        withContext(Dispatchers.Main){
                            val username = userAuthRef.currentUser?.displayName ?: "User"
                            val email = userAuthRef.currentUser?.email.toString()
                            saveUserToDatabase(username, email)

                            Toast.makeText(context, "Login Successful!", Toast.LENGTH_LONG).show()

                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }
                    } catch (e: java.lang.Exception) {
                        withContext(Dispatchers.Main){
                            Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    fun signOut() {
        userAuthRef.signOut()
    }

    fun getFirstName(): String {
        val fullName: List<String>? = userAuthRef.currentUser?.displayName?.split(" ")
        return fullName?.get(0) ?: "User"
    }

    fun addSchedule(schedule: Schedule) {
        
    }

    private fun saveUserToDatabase(username: String, email: String) {
        val user = hashMapOf (
            "id" to email,
            "username" to username
        )

        class FirebaseException(message: String): java.lang.Exception(message)
        userDatabaseRef.collection("users")
            .document(email)
            .set(user)
            .addOnFailureListener {
                throw FirebaseException("Error Inserting User")
            }
    }
}