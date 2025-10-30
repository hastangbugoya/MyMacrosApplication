package com.example.mymacrosapplication.service

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mymacrosapplication.R

class AudioPlayerService : Service() {
    private val binder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    companion object {
        const val CHANNEL_ID = "audio_player_channel"
        const val NOTIFICATION_ID = 101
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
    }

    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        android.util.Log.d("Meow", "AudioPlayerService> onStartCommand> Attempting to play URI: $intent")

        val uri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra("AUDIO_URI", Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent?.getParcelableExtra<Uri>("AUDIO_URI")
            }

        if (uri != null) {
            android.util.Log.d("Meow", "AudioPlayerService> onStartCommand> Received URI: $uri (${uri?.javaClass?.name})")
            playAudio(uri)
        } else {
            android.util.Log.d("Meow", "AudioPlayerService> onStartCommand> No URI received — maybe already playing")
        }

        when (intent?.action) {
            ACTION_PLAY_PAUSE -> togglePlayback()
            ACTION_STOP -> stopSelf()
            else -> {
                val uri = intent?.getParcelableExtra<Uri>("audio_uri")
                uri?.let { playAudio(it) }
            }
        }
        return START_STICKY
    }

    private fun playAudio(uri: Uri) {
        android.util.Log.d("Meow", "AudioPlayerService> playAudio> Attempting to play URI: $uri")

        mediaPlayer?.release()

        mediaPlayer =
            MediaPlayer().apply {
                setDataSource(applicationContext, uri)
                prepare()
                start()

                setOnCompletionListener {
                    this@AudioPlayerService.isPlaying = false
                    showNotification()
                }
            }

        isPlaying = true
        showNotification()
    }

    private fun togglePlayback() {
        val player = mediaPlayer ?: return
        if (isPlaying) {
            player.pause()
        } else {
            player.start()
        }
        isPlaying = !isPlaying
        showNotification()
    }

    @SuppressLint("ForegroundServiceType")
    private fun showNotification() {
        android.util.Log.d("Meow", "AudioPlayerService> showNotification")
        val playPauseAction =
            if (isPlaying) {
                NotificationCompat.Action
                    .Builder(
                        R.drawable.pause_24x24,
                        "Pause",
                        getPendingIntent(ACTION_PLAY_PAUSE),
                    ).build()
            } else {
                NotificationCompat.Action
                    .Builder(
                        R.drawable.play_24x24,
                        "Play",
                        getPendingIntent(ACTION_PLAY_PAUSE),
                    ).build()
            }

        val stopAction =
            NotificationCompat.Action
                .Builder(
                    R.drawable.stop__1_24x24,
                    "Stop",
                    getPendingIntent(ACTION_STOP),
                ).build()

        val notification =
            NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setContentTitle("Now Playing")
                .setContentText(if (isPlaying) "Playing audio" else "Paused")
                .setSmallIcon(R.drawable.music_alt_24x24)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.music_alt))
                .addAction(playPauseAction)
                .addAction(stopAction)
                .setStyle(
                    androidx.media.app.NotificationCompat
                        .MediaStyle(),
                ).setOnlyAlertOnce(true)
                .setOngoing(isPlaying)
                .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent =
            Intent(this, AudioPlayerService::class.java).apply {
                this.action = action
            }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    "AUDIO_CHANNEL",
                    "Audio Playback",
                    NotificationManager.IMPORTANCE_LOW,
                )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
