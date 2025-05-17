package com.example.uitutorial.services

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.media.app.NotificationCompat.MediaStyle
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat

import com.example.uitutorial.R

class RunningServices : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.Start.toString() -> start()
            Actions.Pause.toString() -> pause()
            Actions.Stop.toString() -> stopSelf()
        }
        return START_STICKY
    }

    private fun start() {
        val startIntent = Intent(this, NotificationActionReceiver::class.java).apply { action = "ACTION_START" }
        val startPendingIntent = PendingIntent.getBroadcast(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val pauseIntent = Intent(this, NotificationActionReceiver::class.java).apply { action = "ACTION_PAUSE" }
        val pausePendingIntent = PendingIntent.getBroadcast(this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val stopIntent = Intent(this, NotificationActionReceiver::class.java).apply { action = "ACTION_STOP" }
        val stopPendingIntent = PendingIntent.getBroadcast(this, 2, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Workout in Progress")
            .setContentText("Track your workout progress")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setStyle(MediaStyle().setShowActionsInCompactView(0, 1, 2))
            .addAction(android.R.drawable.ic_media_play, "Spotify Not Found On Device", startPendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Spotify Not Found On Device", pausePendingIntent)
            .addAction(android.R.drawable.ic_delete, "Spotify Not Found On Device", stopPendingIntent)
            .build()

        startForeground(1, notification)
    }

    private fun pause() {

    }

    enum class Actions {
        Start, Pause, Stop
    }
}

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            "ACTION_START" -> {
                Toast.makeText(context, "Workout Started", Toast.LENGTH_SHORT).show()
                // Handle start action (e.g., start workout tracking)
            }
            "ACTION_PAUSE" -> {
                Toast.makeText(context, "Workout Paused", Toast.LENGTH_SHORT).show()
                // Handle pause action (e.g., pause tracking)
            }
            "ACTION_STOP" -> {
                Toast.makeText(context, "Workout Stopped", Toast.LENGTH_SHORT).show()
                // Handle stop action (e.g., stop workout tracking)
            }
        }
    }
}