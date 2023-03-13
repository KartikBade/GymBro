package com.example.gymbro.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.example.gymbro.activity.HomeActivity
import com.example.gymbro.adapter.ExercisesAdapter
import com.example.gymbro.adapter.MySchedulesAdapter
import com.example.gymbro.databinding.FragmentHomeBinding
import com.example.gymbro.databinding.FragmentScheduleBinding
import com.example.gymbro.fragment.REQUEST_CODE_SIGN_IN
import com.example.gymbro.model.Exercise
import com.example.gymbro.model.History
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
import java.text.SimpleDateFormat
import java.util.*

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

    fun getFullName(): String {
        return userAuthRef.currentUser?.displayName ?: "User"
    }

    suspend fun addSchedule(schedule: Schedule) {
        val email = userAuthRef.currentUser?.email.toString()
        val currentSchedule: MutableMap<String, Any> = hashMapOf(
            "name" to schedule.name,
            "description" to schedule.description
        )
        userDatabaseRef.collection("users")
            .document(email)
            .collection("schedules")
            .add(currentSchedule).await()
    }

    fun bindScheduleAdapterToDatabase(adapter: MySchedulesAdapter, binding: FragmentHomeBinding) {
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
                } else {
                    binding.emptyRvScheduleAddButton.visibility = View.INVISIBLE
                }
            }
        }
    }

    suspend fun bindExerciseAdapterToDatabase(adapter: ExercisesAdapter, binding: FragmentScheduleBinding, scheduleName: String) {
        val scheduleCollectionRef = userDatabaseRef.collection("users")
            .document(userAuthRef.currentUser?.email.toString())
            .collection("schedules")

        var docId: String? = null
        scheduleCollectionRef.get()
            .addOnSuccessListener {
                for (document in it) {
                    if (document.data["name"].toString() == scheduleName) {
                        docId = document.id
                        return@addOnSuccessListener
                    }
                }
            }.await()

        docId?.let {
            scheduleCollectionRef.document(it)
                .collection("exercises")
                .addSnapshotListener { value, error ->
                    error?.let {
                        Toast.makeText(context, error.message.toString(), Toast.LENGTH_LONG).show()
                        return@addSnapshotListener
                    }
                    value?.let { result ->
                        val exerciseList = arrayListOf<Exercise>()
                        for (document in result) {
                            exerciseList.add(Exercise(document.data["name"].toString(), document.data["instructions"].toString()))
                        }
                        adapter.submitList(exerciseList)
                        if (exerciseList.size <= 0) {
                            binding.tvEmptyExercisesRv.visibility = View.VISIBLE
                        } else {
                            binding.tvEmptyExercisesRv.visibility = View.INVISIBLE
                        }
                    }
                }
        }
    }

    suspend fun addExercise(exercise: Exercise, scheduleName: String) {
        val currentExercise: MutableMap<String, Any> = hashMapOf(
            "name" to exercise.name,
            "instructions" to exercise.instructions
        )

        val scheduleCollectionRef = userDatabaseRef.collection("users")
            .document(userAuthRef.currentUser?.email.toString())
            .collection("schedules")

        var scheduleDocId: String? = null
        scheduleCollectionRef.get()
            .addOnSuccessListener {
                for (document in it) {
                    if (document.data["name"].toString() == scheduleName) {
                        scheduleDocId = document.id
                    }
                }
            }.await()

        scheduleDocId?.let {
            scheduleCollectionRef.document(it)
                .collection("exercises")
                .add(currentExercise).await()
        }
    }

    suspend fun addLog(scheduleName: String, exerciseName: String, reps: Int, weight: Int) {
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val currentLog: MutableMap<String, Any> = hashMapOf(
            "date" to currentDate,
            "time" to currentTime,
            "reps" to reps,
            "weight" to weight
        )

        val scheduleCollectionRef = userDatabaseRef.collection("users")
            .document(userAuthRef.currentUser?.email.toString())
            .collection("schedules")

        var scheduleDocId: String? = null
        scheduleCollectionRef.get()
            .addOnSuccessListener {
                for (document in it) {
                    if (document.data["name"].toString() == scheduleName) {
                        scheduleDocId = document.id
                    }
                }
            }.await()

        var exerciseDocId: String? = null
        scheduleDocId?.let { it ->
            scheduleCollectionRef.document(it)
                .collection("exercises").get()
                .addOnSuccessListener {
                    for (document in it) {
                        if (document.data["name"].toString() == exerciseName) {
                            exerciseDocId = document.id
                        }
                    }
                }.await()
        }

        exerciseDocId?.let { exerciseId ->
            scheduleDocId?.let { scheduleId ->
                scheduleCollectionRef.document(scheduleId)
                    .collection("exercises")
                    .document(exerciseId)
                    .collection("history")
                    .document(currentDate + currentTime)
                    .set(currentLog).await()
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