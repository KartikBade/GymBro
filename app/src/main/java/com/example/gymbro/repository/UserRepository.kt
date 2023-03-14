package com.example.gymbro.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.gymbro.activity.HomeActivity
import com.example.gymbro.adapter.ExercisesAdapter
import com.example.gymbro.adapter.HistoryAdapter
import com.example.gymbro.adapter.MySchedulesAdapter
import com.example.gymbro.databinding.FragmentExerciseBinding
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
import kotlin.math.log

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
            .document(schedule.name)
            .set(currentSchedule).await()
    }

    fun bindScheduleAdapterToDatabase(adapter: MySchedulesAdapter, binding: FragmentHomeBinding) {
        userDatabaseRef.collection("users")
            .document(userAuthRef.currentUser?.email.toString())
            .collection("schedules")
            .addSnapshotListener { value, error ->
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

    fun bindExerciseAdapterToDatabase(adapter: ExercisesAdapter, binding: FragmentScheduleBinding, scheduleName: String) {
        userDatabaseRef.collection("users")
            .document(userAuthRef.currentUser?.email.toString())
            .collection("schedules")
            .document(scheduleName)
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

    suspend fun addExercise(exercise: Exercise, scheduleName: String) {
        val currentExercise: MutableMap<String, Any> = hashMapOf(
            "name" to exercise.name,
            "instructions" to exercise.instructions
        )

        userDatabaseRef.collection("users")
            .document(userAuthRef.currentUser?.email.toString())
            .collection("schedules")
            .document(scheduleName)
            .collection("exercises")
            .document(exercise.name)
            .set(currentExercise).await()
    }

    suspend fun addLog(scheduleName: String, exerciseName: String, reps: Int, weight: Int) {
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val currentLog: MutableMap<String, Any> = hashMapOf(
            "id" to currentDate+currentTime,
            "date" to currentDate,
            "time" to currentTime,
            "reps" to reps,
            "weight" to weight
        )

        userDatabaseRef.collection("users")
            .document(userAuthRef.currentUser?.email.toString())
            .collection("schedules")
            .document(scheduleName)
            .collection("exercises")
            .document(exerciseName)
            .collection(currentDate)
            .add(currentLog).await()
    }

    fun bindHistoryAdapterToDatabase(
        adapter: HistoryAdapter,
        binding: FragmentExerciseBinding,
        scheduleName: String,
        exerciseName: String,
    ) {
        val date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        userDatabaseRef.collection("users")
            .document(userAuthRef.currentUser?.email.toString())
            .collection("schedules")
            .document(scheduleName)
            .collection("exercises")
            .document(exerciseName)
            .collection(date)
            .addSnapshotListener { value, error ->
                error?.let {
                    Toast.makeText(context, error.message.toString(), Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                value?.let { result ->
                    val logList = arrayListOf<History>()
                    for (document in result) {
                        logList.add(
                            History(
                                document.data["date"].toString(),
                                document.data["time"].toString(),
                                document.data["reps"].toString().toInt(),
                                document.data["weight"].toString().toInt()
                            )
                        )
                    }
                    logList.sortBy {
                        val timeAsInt = it.time.subSequence(0, 2).toString() +
                                it.time.subSequence(3, 5).toString() +
                                it.time.subSequence(6, 8).toString()
                        timeAsInt
                    }
                    adapter.submitList(logList)
//                    if (exerciseList.size <= 0) {
//                        binding.tvEmptyExercisesRv.visibility = View.VISIBLE
//                    } else {
//                        binding.tvEmptyExercisesRv.visibility = View.INVISIBLE
//                    }
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