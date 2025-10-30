package com.example.mymacrosapplication.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mymacrosapplication.R

class NotificationHelper(
    private val context: Context,
) {
    private val channelId = "audio_playback_channel"

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    channelId,
                    "Audio Playback",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    description = "Shows playback controls for your songs"
                    lightColor = Color.BLUE
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
            val nm = context.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    fun buildNotification(
        title: String,
        artist: String,
    ): Notification =
        NotificationCompat
            .Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(R.drawable.headphones_rhythm_24x24)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
}
