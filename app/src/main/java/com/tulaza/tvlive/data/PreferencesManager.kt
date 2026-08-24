package com.tulaza.tvlive.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "tvlive_prefs")

class PreferencesManager(private val context: Context) {

    private object Keys {
        val PLAYLISTS = stringSetPreferencesKey("m3u_playlists") // "nombre|||url"
        val FAVORITES = stringSetPreferencesKey("favorite_ids")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val playlists: Flow<List<M3uPlaylist>> = context.dataStore.data.map { prefs ->
        (prefs[Keys.PLAYLISTS] ?: emptySet()).mapNotNull { encoded ->
            val parts = encoded.split("|||")
            if (parts.size == 2) M3uPlaylist(id = encoded, name = parts[0], url = parts[1]) else null
        }
    }

    val favorites: Flow<Set<String>> = context.dataStore.data.map { it[Keys.FAVORITES] ?: emptySet() }

    val darkMode: Flow<Boolean?> = context.dataStore.data.map { it[Keys.DARK_MODE] }

    suspend fun addPlaylist(name: String, url: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.PLAYLISTS] ?: emptySet()
            prefs[Keys.PLAYLISTS] = current + "$name|||$url"
        }
    }

    suspend fun removePlaylist(playlist: M3uPlaylist) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.PLAYLISTS] ?: emptySet()
            prefs[Keys.PLAYLISTS] = current - playlist.id
        }
    }

    suspend fun toggleFavorite(channelId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.FAVORITES] ?: emptySet()
            prefs[Keys.FAVORITES] = if (channelId in current) current - channelId else current + channelId
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.DARK_MODE] = enabled }
    }
}
