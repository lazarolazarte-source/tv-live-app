package com.tulaza.tvlive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tulaza.tvlive.data.BuiltInChannels
import com.tulaza.tvlive.data.Channel
import com.tulaza.tvlive.data.M3uParser
import com.tulaza.tvlive.data.M3uPlaylist
import com.tulaza.tvlive.data.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class ImportState {
    object Idle : ImportState()
    object Loading : ImportState()
    data class Success(val count: Int) : ImportState()
    data class Error(val message: String) : ImportState()
}

data class UiState(
    val allChannels: List<Channel> = emptyList(),
    val playlists: List<M3uPlaylist> = emptyList(),
    val favorites: Set<String> = emptySet(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val darkMode: Boolean = true,
    val importState: ImportState = ImportState.Idle
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)

    private val _state = MutableStateFlow(UiState(allChannels = BuiltInChannels.defaultChannels))
    val state: StateFlow<UiState> = _state

    private var importedChannels: List<Channel> = emptyList()

    init {
        viewModelScope.launch {
            combine(prefs.playlists, prefs.favorites, prefs.darkMode) { playlists, favs, dark ->
                Triple(playlists, favs, dark)
            }.collect { (playlists, favs, dark) ->
                _state.update {
                    it.copy(
                        playlists = playlists,
                        favorites = favs,
                        darkMode = dark ?: true
                    )
                }
                // Re-sincroniza los canales importados con las listas guardadas
                refreshImportedChannels(playlists)
            }
        }
    }

    private fun refreshImportedChannels(playlists: List<M3uPlaylist>) {
        viewModelScope.launch {
            val results = mutableListOf<Channel>()
            withContext(Dispatchers.IO) {
                playlists.forEach { playlist ->
                    runCatching { M3uParser.fetchAndParse(playlist.url, playlist.name) }
                        .onSuccess { results.addAll(it) }
                }
            }
            importedChannels = results
            _state.update { it.copy(allChannels = BuiltInChannels.defaultChannels + importedChannels) }
        }
    }

    fun addM3uPlaylist(name: String, url: String) {
        viewModelScope.launch {
            _state.update { it.copy(importState = ImportState.Loading) }
            val result = withContext(Dispatchers.IO) {
                runCatching { M3uParser.fetchAndParse(url, name) }
            }
            result.onSuccess { channels ->
                prefs.addPlaylist(name, url)
                _state.update { it.copy(importState = ImportState.Success(channels.size)) }
            }.onFailure { e ->
                _state.update { it.copy(importState = ImportState.Error(e.message ?: "Error al importar la lista")) }
            }
        }
    }

    fun removePlaylist(playlist: M3uPlaylist) {
        viewModelScope.launch { prefs.removePlaylist(playlist) }
    }

    fun resetImportState() {
        _state.update { it.copy(importState = ImportState.Idle) }
    }

    fun toggleFavorite(channelId: String) {
        viewModelScope.launch { prefs.toggleFavorite(channelId) }
    }

    fun setSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun setCategory(category: String?) {
        _state.update { it.copy(selectedCategory = category) }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { prefs.setDarkMode(enabled) }
    }
}
