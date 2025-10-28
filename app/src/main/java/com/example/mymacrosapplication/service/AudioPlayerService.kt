package com.example.mymacrosapplication.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder

class AudioPlayerService : Service() {
    private val binder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null

    // 👇 Inner binder class
    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    fun playAudio(uri: Uri) {
        mediaPlayer?.release()
        mediaPlayer =
            MediaPlayer().apply {
                setDataSource(applicationContext, uri)
                prepare()
                start()
            }
    }

    fun playPause() {
        mediaPlayer?.let {
            if (it.isPlaying) it.pause() else it.start()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}
