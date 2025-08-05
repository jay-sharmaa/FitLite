package com.example.fitlite.viewModels

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

val Context.dataStore by preferencesDataStore(name = "user_preferences")

class HomePageViewModel(private val context: Context) : ViewModel() {

    private val _daysWorkout = MutableStateFlow(0)
    val daysWorkout: StateFlow<Int> = _daysWorkout.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user"

    private val _weekWorkoutDates = MutableStateFlow<List<String>>(emptyList())
    val weekWorkoutDates: StateFlow<List<String>> = _weekWorkoutDates

    // Load the stored workout days from DataStore when the ViewModel is initialized
    init {
        viewModelScope.launch {
            fetchWorkoutDates()
        }
        viewModelScope.launch {
            try {
                getDaysWorkout(context).collect {
                    _daysWorkout.value = it
                }
            } catch (e: Exception) {
                Log.e("ViewModelInit", "Error in init block", e)
            }
        }
    }

    // Call this function to update the value in ViewModel and store it in DataStore
    fun setDaysWorkout(days: Int) {
        viewModelScope.launch {
            _daysWorkout.value = days
            saveDaysWorkout(context, days)
        }
    }

    private suspend fun fetchWorkoutDates() {
        try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            val dates = snapshot.get("workoutDates") as? List<String> ?: emptyList()

            Log.d("RawDates", "From Firestore: $dates")

            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val today = Calendar.getInstance()
            val dayOfWeek = today.get(Calendar.DAY_OF_WEEK)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -(dayOfWeek - Calendar.SUNDAY))
            val startOfWeek = formatter.format(calendar.time)

            calendar.add(Calendar.DAY_OF_YEAR, 6)
            val endOfWeek = formatter.format(calendar.time)

            Log.d("WeekRange", "Start: $startOfWeek, End: $endOfWeek")

            val weekDates = dates.filter {
                try {
                    val date = formatter.parse(it)
                    date != null &&
                            formatter.format(date) >= startOfWeek &&
                            formatter.format(date) <= endOfWeek
                } catch (e: Exception) {
                    Log.e("ParseFail", "Couldn't parse $it", e)
                    false
                }
            }

            Log.d("FilteredDates", "This week's dates: $weekDates")

            _weekWorkoutDates.value = weekDates.distinct()
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching workoutDates", e)
            _weekWorkoutDates.value = emptyList()
        }
    }

    private fun getDaysWorkout(context: Context): Flow<Int> {
        Log.d("DaysWorkout", "true")
        return context.dataStore.data
            .map { preferences ->
                preferences[DAYS_WORKOUT_KEY] ?: 0
            }
    }

    private suspend fun saveDaysWorkout(context: Context, daysWorkout: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAYS_WORKOUT_KEY] = daysWorkout
        }
    }

    private companion object {
        val DAYS_WORKOUT_KEY = intPreferencesKey("day_workout")
    }
}
