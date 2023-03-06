package com.example.gymbro.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.gymbro.activity.HomeActivity
import com.example.gymbro.adapter.MySchedulesAdapter
import com.example.gymbro.databinding.FragmentHomeBinding
import com.example.gymbro.fragment.REQUEST_CODE_SIGN_IN
import com.example.gymbro.model.Schedule
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
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
        val email = userAuthRef.currentUser?.email.toString()
        val currentSchedule: MutableMap<String, Any> = hashMapOf(
            "name" to schedule.name,
            "description" to schedule.description
        )
        userDatabaseRef.collection("users")
            .document(email)
            .collection("schedules")
            .add(currentSchedule)
    }

//    suspend fun getScheduleList(): ArrayList<Schedule> {
//        val scheduleList: ArrayList<Schedule> = ArrayList()
//        userDatabaseRef.collection("users")
//            .document(userAuthRef.currentUser?.email.toString())
//            .collection("schedules")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val name = document.data["name"].toString()
//                    val desc = document.data["description"].toString()
//                    Log.e("UserRepo", "$name -> $desc")
//                    scheduleList.add(Schedule(name = name, description = desc))
//                    Log.e("UserRepo", scheduleList.size.toString())
//                }
//            }.await()
//        Log.e("UserRepo", scheduleList.size.toString())
//        return scheduleList
//    }

    fun bindAdapterToDatabase(adapter: MySchedulesAdapter, binding: FragmentHomeBinding) {
        val scheduleCollectionRef = userDatabaseRef.collection("users")
            .document(userAuthRef.currentUser?.email.toString())
            .collection("schedules")

        scheduleCollectionRef.addSnapshotListener { value, error ->
            error?.let {
                Toast.makeText(context, error.message.toString(), Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val scheduleList = arrayListOf<Schedule>()
                for (document in it) {
                    scheduleList.add(Schedule(document.data["name"].toString(), document.data["description"].toString()))
                }
                adapter.submitList(scheduleList)
                if (scheduleList.size <= 0) {
                    binding.emptyRvScheduleAddButton.visibility = View.VISIBLE
                    binding.emptyRvScheduleTextView.visibility = View.VISIBLE
                } else {
                    binding.emptyRvScheduleAddButton.visibility = View.INVISIBLE
                    binding.emptyRvScheduleTextView.visibility = View.INVISIBLE
                }
            }
        }
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