package com.example.fitlite.presentation.ViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.data.SampleDataPoint
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.unregisterMeasureCallback
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore(name = "user_data")

class BackgroundViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val healthServicesManager = HealthServicesManager(context)

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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun readHeartRate(){
        viewModelScope.launch {
            healthServicesManager.heartRateMeasureFlow().collect { measureMessage ->
                when (measureMessage) {
                    is MeasureMessage.MeasureData -> {
                        val bpm = measureMessage.data.firstOrNull()?.value?.toInt() ?: 0
                        _heartRate.value = bpm
                        Log.d("HeartRate", "Updated Heart Rate: $bpm BPM")
                        setHeartRate(context, bpm) // Save to DataStore
                    }
                    is MeasureMessage.MeasureAvailability -> {
                        Log.d("HeartRate", "Availability: ${measureMessage.availability}")
                    }
                }
            }
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

class HealthServicesManager(context: Context) {
    private val measureClient = HealthServices.getClient(context).measureClient

    suspend fun hasHeartRateCapability() = runCatching {
        val capabilities = measureClient.getCapabilities()
        (DataType.HEART_RATE_BPM in capabilities.supportedDataTypesMeasure)
    }.getOrDefault(false)

    @ExperimentalCoroutinesApi
    fun heartRateMeasureFlow(): Flow<MeasureMessage> = callbackFlow {
        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(dataType: DeltaDataType<*, *>, availability: Availability) {
                if (availability is DataTypeAvailability) {
                    trySendBlocking(MeasureMessage.MeasureAvailability(availability))
                }
            }

            override fun onDataReceived(data: DataPointContainer) {
                val heartRateBpm = data.getData(DataType.HEART_RATE_BPM)
                Log.d("message","💓 Received heart rate: ${heartRateBpm.first().value}")
                trySendBlocking(MeasureMessage.MeasureData(heartRateBpm))
            }
        }

        Log.d("message", "⌛ Registering for data...")
        measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)

        awaitClose {
            Log.d("message", "👋 Unregistering for data")
            runBlocking {
                measureClient.unregisterMeasureCallback(DataType.HEART_RATE_BPM, callback)
            }
        }
    }
}

sealed class MeasureMessage {
    class MeasureAvailability(val availability: DataTypeAvailability) : MeasureMessage()
    class MeasureData(val data: List<SampleDataPoint<Double>>) : MeasureMessage()
}