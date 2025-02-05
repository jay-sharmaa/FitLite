package com.example.uitutorial.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "user_preferences")


class HomePageViewModel(private val context: Context) : ViewModel() {

    private val _daysWorkout = MutableStateFlow(0)
    val daysWorkout: StateFlow<Int> = _daysWorkout.asStateFlow()

    // Load the stored workout days from DataStore when the ViewModel is initialized
    init {

        viewModelScope.launch {
            getDaysWorkout(context).collect {
                _daysWorkout.value = it
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

    // Get the stored workout days from DataStore
    private fun getDaysWorkout(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[DAYS_WORKOUT_KEY] ?: 0 // Default value is 0 if not found
            }
    }

    // Save the workout days to DataStore
    private suspend fun saveDaysWorkout(context: Context, daysWorkout: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAYS_WORKOUT_KEY] = daysWorkout
        }
    }

    private companion object {
        val DAYS_WORKOUT_KEY = intPreferencesKey("day_workout")
    }
}
