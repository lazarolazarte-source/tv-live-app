package com.tulaza.tvlive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tulaza.tvlive.viewmodel.ImportState
import com.tulaza.tvlive.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaylistScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()

    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    LaunchedEffect(state.importState) {
        if (state.importState is ImportState.Success) {
            name = ""
            url = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listas M3U externas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                "Agregá la URL de tu propia lista M3U/M3U8 (por ejemplo, una lista IPTV que ya tengas contratada o autorizada). " +
                    "La app no incluye ni recomienda listas de terceros: sos responsable del contenido que cargues.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la lista") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL (.m3u / .m3u8)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { viewModel.addM3uPlaylist(name.ifBlank { "Mi lista" }, url) },
                enabled = url.isNotBlank() && state.importState !is ImportState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.importState is ImportState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text("Importar lista")
            }

            when (val importState = state.importState) {
                is ImportState.Success -> Text(
                    "¡Listo! Se importaron ${importState.count} canales.",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                is ImportState.Error -> Text(
                    "Error: ${importState.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
                else -> {}
            }

            Spacer(Modifier.height(24.dp))
            Text("Tus listas guardadas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            if (state.playlists.isEmpty()) {
                Text(
                    "Todavía no agregaste ninguna lista.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.playlists, key = { it.id }) { playlist ->
                        Card(shape = RoundedCornerShape(14.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.PlaylistPlay, contentDescription = null)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(playlist.name, fontWeight = FontWeight.Medium)
                                    Text(
                                        playlist.url,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                                IconButton(onClick = { viewModel.removePlaylist(playlist) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
