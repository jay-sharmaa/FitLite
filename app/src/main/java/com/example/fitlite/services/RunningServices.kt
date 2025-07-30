package com.example.fitlite.services

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.android.fitlite.R
import java.util.Locale

class RunningServices : Service() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private var isListening = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.Start.toString() -> {
                start()
                startSpeechRecognition()
            }
            Actions.Pause.toString() -> stopSpeechRecognition()
            Actions.Stop.toString() -> {
                stopSpeechRecognition()
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun start() {
        val startIntent = Intent(this, NotificationActionReceiver::class.java).apply { action = "ACTION_START" }
        val pauseIntent = Intent(this, NotificationActionReceiver::class.java).apply { action = "ACTION_PAUSE" }
        val stopIntent = Intent(this, NotificationActionReceiver::class.java).apply { action = "ACTION_STOP" }

        val startPendingIntent = PendingIntent.getBroadcast(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val pausePendingIntent = PendingIntent.getBroadcast(this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val stopPendingIntent = PendingIntent.getBroadcast(this, 2, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Workout in Progress")
            .setContentText("Listening for voice commands...")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setStyle(MediaStyle().setShowActionsInCompactView(0, 1, 2))
            .addAction(android.R.drawable.ic_media_play, "Start", startPendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Pause", pausePendingIntent)
            .addAction(android.R.drawable.ic_delete, "Stop", stopPendingIntent)
            .build()

        startForeground(1, notification)
    }

    private fun startSpeechRecognition() {
        if (isListening) return

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val spokenText = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: ""
                Log.d("Speech", "Recognized: $spokenText")

                if("escape" == spokenText.lowercase())
                    Toast.makeText(applicationContext, "Heard: $spokenText", Toast.LENGTH_SHORT).show()

                startListening()
            }

            override fun onError(error: Int) {
                Log.e("Speech", "Error code: $error")
                startListening()
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        startListening()
    }

    private fun startListening() {
        if (::speechRecognizer.isInitialized) {
            isListening = true
            speechRecognizer.startListening(speechIntent)
        }
    }

    private fun stopSpeechRecognition() {
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            speechRecognizer.destroy()
        }
        isListening = false
    }

    override fun onDestroy() {
        stopSpeechRecognition()
        super.onDestroy()
    }
    enum class Actions {
        Start, Pause, Stop
    }
}
class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            "ACTION_START" -> {
                Toast.makeText(context, "Song Started", Toast.LENGTH_SHORT).show()
            }
            "ACTION_PAUSE" -> {
                Toast.makeText(context, "Song Paused", Toast.LENGTH_SHORT).show()
            }
            "ACTION_STOP" -> {
                Toast.makeText(context, "Song Cancel", Toast.LENGTH_SHORT).show()
            }
        }
    }
}