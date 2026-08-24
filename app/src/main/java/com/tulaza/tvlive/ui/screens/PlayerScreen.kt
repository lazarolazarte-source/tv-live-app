package com.tulaza.tvlive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tulaza.tvlive.data.Channel
import com.tulaza.tvlive.data.SourceType
import com.tulaza.tvlive.player.ExoStreamPlayer
import com.tulaza.tvlive.player.YoutubeLivePlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(channel: Channel, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(channel.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (channel.sourceType) {
                SourceType.YOUTUBE_LIVE -> YoutubeLivePlayer(youtubeChannelId = channel.source)
                SourceType.STREAM -> ExoStreamPlayer(streamUrl = channel.source)
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(channel.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    listOfNotNull(
                        channel.country.ifBlank { null },
                        channel.category,
                        channel.playlistOrigin?.let { "Lista: $it" }
                    ).joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
