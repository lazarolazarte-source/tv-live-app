package com.tulaza.tvlive.data

/**
 * Tipo de fuente del canal.
 * YOUTUBE_LIVE -> se reproduce embebido con el reproductor oficial de YouTube (IFrame API).
 * STREAM -> URL directa (m3u8/HLS u otro stream) proveniente de una lista M3U agregada por el usuario.
 */
enum class SourceType { YOUTUBE_LIVE, STREAM }

data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String? = null,
    val category: String = "General",
    val sourceType: SourceType,
    // Para YOUTUBE_LIVE: videoId o handle del canal. Para STREAM: URL del stream.
    val source: String,
    val country: String = "",
    val playlistOrigin: String? = null // nombre de la lista M3U de la que proviene (null = integrado)
)

data class M3uPlaylist(
    val id: String,
    val name: String,
    val url: String,
    val channelCount: Int = 0
)
