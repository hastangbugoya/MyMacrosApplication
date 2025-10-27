package com.example.mymacrosapplication.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.mymacrosapplication.R // adjust if needed

class AudioPlayerService : Service() {
    private val binder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isPrepared = false // <-- Track manually

    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()

        // Load your MP3 file from res/raw/
        if (mediaPlayer == null) {
            mediaPlayer =
                MediaPlayer.create(this, R.raw.sample_song).apply {
                    setOnCompletionListener { this@AudioPlayerService.isPlaying = false }
                    isPrepared = true
                }
        }
    }

    fun playPause() {
        val player = mediaPlayer ?: return

        if (!isPrepared) return

        if (isPlaying) {
            player.pause()
        } else {
            player.start()
        }
        isPlaying = !isPlaying
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}
