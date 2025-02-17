package com.example.fitlite.presentation.ViewModel

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "user_data")

class BackgroundViewModel(context: Context) : ViewModel() {
    private val _heartRate = MutableStateFlow(0)
    private val _walkDistance = MutableStateFlow(0)
    private val _runDistance = MutableStateFlow(0)
    private val _oxygenLevel = MutableStateFlow(0)
    private val _exercise = MutableStateFlow(0)

    val heartRate : StateFlow<Int> = _heartRate.asStateFlow()
    val walkDistance : StateFlow<Int> = _walkDistance.asStateFlow()
    val runDistance : StateFlow<Int> = _runDistance.asStateFlow()
    val oxygenLevel : StateFlow<Int> = _oxygenLevel.asStateFlow()
    val exercise : StateFlow<Int> = _exercise.asStateFlow()

    init{
        viewModelScope.launch {
            getHeartRate(context).collect{
                _heartRate.value = it
            }
            getWalkDistance(context).collect { _walkDistance.value = it }
            getRunDistance(context).collect { _runDistance.value = it }
            getOxygenLevel(context).collect { _oxygenLevel.value = it }
            getExercise(context).collect { _exercise.value = it }
        }
    }

    private fun getHeartRate(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[HEART_RATE_KEY] ?: 0 // Default value is 0 if not found
            }
    }

    private fun getWalkDistance(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[WALK_DISTANCE_KEY] ?: 0 // Default value is 0 if not found
            }
    }

    private fun getRunDistance(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[RUN_DISTANCE_KEY] ?: 0 // Default value is 0 if not found
            }
    }

    private fun getOxygenLevel(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[OXYGEN_LEVEL_KEY] ?: 0 // Default value is 0 if not found
            }
    }

    private fun getExercise(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[EXERCISE_KEY] ?: 0 // Default value is 0 if not found
            }
    }

    fun setHeartRate(context: Context, value: Int) {
        viewModelScope.launch {
            context.dataStore.edit { it[HEART_RATE_KEY] = value }
        }
    }

    fun setWalkDistance(context: Context, value: Int) {
        viewModelScope.launch {
            context.dataStore.edit { it[WALK_DISTANCE_KEY] = value }
        }
    }

    fun setRunDistance(context: Context, value: Int) {
        viewModelScope.launch {
            context.dataStore.edit { it[RUN_DISTANCE_KEY] = value }
        }
    }

    fun setOxygenLevel(context: Context, value: Int) {
        viewModelScope.launch {
            context.dataStore.edit { it[OXYGEN_LEVEL_KEY] = value }
        }
    }

    fun setExercise(context: Context, value: Int) {
        viewModelScope.launch {
            context.dataStore.edit { it[EXERCISE_KEY] = value }
        }
    }

    private companion object {
        val HEART_RATE_KEY = intPreferencesKey("heart_rate")
        val WALK_DISTANCE_KEY = intPreferencesKey("walk_distance")
        val RUN_DISTANCE_KEY = intPreferencesKey("run_distance")
        val OXYGEN_LEVEL_KEY = intPreferencesKey("oxygen_level")
        val EXERCISE_KEY = intPreferencesKey("exercise")
    }

}