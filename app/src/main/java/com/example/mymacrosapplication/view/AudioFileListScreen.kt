package com.example.mymacrosapplication.view.mediaplayer

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mymacrosapplication.service.AudioPlayerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// --- Data model ---
data class AudioFile(
    val id: Long,
    val title: String,
    val artist: String?,
    val contentUri: Uri,
)

// --- Query local audio files ---
fun getLocalAudioFiles(context: Context): List<AudioFile> {
    val audioList = mutableListOf<AudioFile>()
    val projection =
        arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
        )
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

    val query =
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder,
        )

    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val title = cursor.getString(titleColumn)
            val artist = cursor.getString(artistColumn)
            val uri =
                ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id,
                )
            audioList.add(AudioFile(id, title, artist, uri))
        }
    }

    return audioList
}

// --- The Composable that lists and triggers service playback ---
@Composable
fun AudioFileListScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var audioFiles by remember { mutableStateOf<List<AudioFile>>(emptyList()) }
    var currentlyPlaying by remember { mutableStateOf<AudioFile?>(null) }

    // Load songs
    LaunchedEffect(Unit) {
        audioFiles = withContext(Dispatchers.IO) { getLocalAudioFiles(context) }
    }

    Column(modifier = modifier.fillMaxSize().padding(8.dp)) {
        Text(
            text = "Local MP3 Files",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(audioFiles) { file ->
                AudioFileItem(
                    file = file,
                    isPlaying = file == currentlyPlaying,
                    onClick = {
                        val serviceIntent =
                            Intent(context, AudioPlayerService::class.java).apply {
                                putExtra("AUDIO_URI", file.contentUri.toString())
                            }
                        context.startService(serviceIntent)
                        currentlyPlaying = file
                    },
                )
            }
        }
    }
}

// --- UI for a single file ---
@Composable
fun AudioFileItem(
    file: AudioFile,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isPlaying) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
            ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = file.title, style = MaterialTheme.typography.bodyLarge)
            file.artist?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            if (isPlaying) {
                Text(
                    text = "▶ Playing...",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
