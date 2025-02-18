package com.example.fitlite.presentation.ViewModel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackgroundViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _heartRate = MutableStateFlow(0)
    val heartRate: StateFlow<Int> = _heartRate.asStateFlow()

    private val _walkDistance = MutableStateFlow(0)
    val walkDistance: StateFlow<Int> = _walkDistance.asStateFlow()

    private val _runDistance = MutableStateFlow(0)
    val runDistance: StateFlow<Int> = _runDistance.asStateFlow()

    private val _oxygenLevel = MutableStateFlow(0)
    val oxygenLevel: StateFlow<Int> = _oxygenLevel.asStateFlow()

    private val _exercise = MutableStateFlow(0)
    val exercise: StateFlow<Int> = _exercise.asStateFlow()

    init {
        viewModelScope.launch {
            collectData(HEART_RATE_KEY, _heartRate)
            collectData(WALK_DISTANCE_KEY, _walkDistance)
            collectData(RUN_DISTANCE_KEY, _runDistance)
            collectData(OXYGEN_LEVEL_KEY, _oxygenLevel)
            collectData(EXERCISE_KEY, _exercise)
        }
    }

    private fun collectData(key: Preferences.Key<Int>, stateFlow: MutableStateFlow<Int>) {
        viewModelScope.launch {
            dataStore.data.map { it[key] ?: 0 }.collectLatest { stateFlow.value = it }
        }
    }

    private suspend fun setData(key: Preferences.Key<Int>, value: Int) {
        dataStore.edit { it[key] = value }
    }

    fun setHeartRate(value: Int) = viewModelScope.launch { setData(HEART_RATE_KEY, value) }
    fun setWalkDistance(value: Int) = viewModelScope.launch { setData(WALK_DISTANCE_KEY, value) }
    fun setRunDistance(value: Int) = viewModelScope.launch { setData(RUN_DISTANCE_KEY, value) }
    fun setOxygenLevel(value: Int) = viewModelScope.launch { setData(OXYGEN_LEVEL_KEY, value) }
    fun setExercise(value: Int) = viewModelScope.launch { setData(EXERCISE_KEY, value) }

    companion object {
        private val HEART_RATE_KEY = intPreferencesKey("heart_rate")
        private val WALK_DISTANCE_KEY = intPreferencesKey("walk_distance")
        private val RUN_DISTANCE_KEY = intPreferencesKey("run_distance")
        private val OXYGEN_LEVEL_KEY = intPreferencesKey("oxygen_level")
        private val EXERCISE_KEY = intPreferencesKey("exercise")
    }
}
