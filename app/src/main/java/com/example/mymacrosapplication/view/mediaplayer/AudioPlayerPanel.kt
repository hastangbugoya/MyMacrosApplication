package com.example.mymacrosapplication.view.mediaplayer

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun AudioPlayerPanel(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    audioUri: Uri,
) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    // Initialize and release player properly
    DisposableEffect(audioUri) {
        mediaPlayer = MediaPlayer.create(context, audioUri)

        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Column(
        modifier = modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = artist, style = MaterialTheme.typography.bodyMedium)

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(onClick = {
                val player = mediaPlayer ?: return@Button
                if (isPlaying) {
                    player.pause()
                } else {
                    player.start()
                }
                isPlaying = !isPlaying
            }) {
                Text(if (isPlaying) "Pause" else "Play")
            }
        }
    }
}
