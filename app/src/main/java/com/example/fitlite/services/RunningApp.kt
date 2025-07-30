package com.example.fitlite.services

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.fitlite.data.AppDatabase
import com.example.fitlite.data.PersonRepository

class RunningApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "running_channel",
            "Running_Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { PersonRepository(database.personDao()) }
}
