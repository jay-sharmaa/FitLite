package com.example.fitlite.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import com.example.fitlite.myDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class WorkoutSettingsRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user"

    private val SELECTED_GENDER = stringPreferencesKey("selected_gender")
    private val SELECTED_REST_TIME = intPreferencesKey("selected_rest_time")
    private val SELECTED_HEIGHT = intPreferencesKey("selected_height")
    private val SELECTED_WEIGHT = intPreferencesKey("selected_weight")

    // Public accessors
    fun getGender(): Flow<String> = syncAndCache("gender", SELECTED_GENDER, "Not set")
    fun getRestTime(): Flow<Int> = syncAndCache("restTime", SELECTED_REST_TIME, 0)
    fun getHeight(): Flow<Int> = syncAndCache("height", SELECTED_HEIGHT, 0)
    fun getWeight(): Flow<Int> = syncAndCache("weight", SELECTED_WEIGHT, 0)

    private fun <T> syncAndCache(
        field: String,
        prefKey: Preferences.Key<T>,
        default: T
    ): Flow<T> {
        return context.myDataStore.data
            .map { it[prefKey] ?: default }
            .onStart {
                try {
                    val docRef = firestore.collection("users").document(userId)
                    val snapshot = docRef.get().await()

                    if (!snapshot.exists()) {
                        docRef.set(mapOf(field to default)).await()
                    } else if (snapshot.contains(field)) {
                        // Document exists, sync field to DataStore
                        val value = snapshot.get(field)
                        context.myDataStore.edit { prefs ->
                            when {
                                value is Long && default is Int -> prefs[prefKey] = value.toInt() as T
                                value is String && default is String -> prefs[prefKey] = value as T
                                value is Int && default is Int -> prefs[prefKey] = value as T
                            }
                        }
                    } else {
                        docRef.update(field, default).await()
                    }
                } catch (_: Exception) {

                }
            }
    }

    suspend fun saveToFirebaseAndCache(field: String, value: Any, key: Preferences.Key<*>) {
        firestore.collection("users").document(userId).update(field, value)
        context.myDataStore.edit { prefs ->
            when (value) {
                is String -> prefs[key as Preferences.Key<String>] = value
                is Int -> prefs[key as Preferences.Key<Int>] = value
            }
        }
    }

    fun getGenderKey() = SELECTED_GENDER
    fun getRestTimeKey() = SELECTED_REST_TIME
    fun getHeightKey() = SELECTED_HEIGHT
    fun getWeightKey() = SELECTED_WEIGHT
}