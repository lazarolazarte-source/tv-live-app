package com.tulaza.tvlive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tulaza.tvlive.data.Channel
import com.tulaza.tvlive.ui.components.ChannelCard
import com.tulaza.tvlive.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onChannelClick: (Channel) -> Unit,
    onAddPlaylistClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    val categories = remember(state.allChannels) {
        listOf("Todos", "Favoritos") + state.allChannels.map { it.category }.distinct().sorted()
    }

    val filteredChannels = remember(state.allChannels, state.searchQuery, state.selectedCategory, state.favorites) {
        state.allChannels.filter { channel ->
            val matchesQuery = state.searchQuery.isBlank() ||
                channel.name.contains(state.searchQuery, ignoreCase = true) ||
                channel.country.contains(state.searchQuery, ignoreCase = true)

            val matchesCategory = when (state.selectedCategory) {
                null, "Todos" -> true
                "Favoritos" -> channel.id in state.favorites
                else -> channel.category == state.selectedCategory
            }
            matchesQuery && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LiveTv, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("TV Live", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onAddPlaylistClick) {
                        Icon(Icons.Filled.PlaylistAdd, contentDescription = "Agregar lista M3U")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::setSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar canal o país...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val selected = (state.selectedCategory ?: "Todos") == category
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.setCategory(if (category == "Todos") null else category) },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (filteredChannels.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron canales", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredChannels, key = { it.id }) { channel ->
                        ChannelCard(
                            channel = channel,
                            isFavorite = channel.id in state.favorites,
                            onClick = { onChannelClick(channel) },
                            onToggleFavorite = { viewModel.toggleFavorite(channel.id) }
                        )
                    }
                }
            }
        }
    }
}
