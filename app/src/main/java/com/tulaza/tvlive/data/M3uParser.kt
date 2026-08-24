package com.tulaza.tvlive.data

import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID

/**
 * Parser genérico de listas M3U/M3U8 (formato estándar IPTV: #EXTM3U / #EXTINF).
 * No incluye ni sugiere ninguna lista propia: el usuario aporta la URL y es
 * responsable del contenido y de que tenga derecho a transmitirlo/verlo.
 */
object M3uParser {

    private val client = OkHttpClient()

    fun fetchAndParse(url: String, playlistName: String): List<Channel> {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("No se pudo descargar la lista (HTTP ${response.code})")
            }
            val body = response.body?.string() ?: throw IllegalStateException("Lista vacía")
            return parse(body, playlistName)
        }
    }

    fun parse(content: String, playlistName: String): List<Channel> {
        val lines = content.lines()
        val channels = mutableListOf<Channel>()

        var currentName: String? = null
        var currentLogo: String? = null
        var currentGroup: String = "General"

        for (rawLine in lines) {
            val line = rawLine.trim()
            when {
                line.startsWith("#EXTINF") -> {
                    currentName = extractAttr(line, "tvg-name")
                        ?: line.substringAfterLast(",").trim().ifBlank { null }
                    currentLogo = extractAttr(line, "tvg-logo")
                    currentGroup = extractAttr(line, "group-title") ?: "General"
                }
                line.isNotEmpty() && !line.startsWith("#") -> {
                    // Esta línea es la URL del stream, cierra la entrada #EXTINF anterior
                    val name = currentName ?: "Canal ${channels.size + 1}"
                    channels.add(
                        Channel(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            logoUrl = currentLogo,
                            category = currentGroup,
                            sourceType = SourceType.STREAM,
                            source = line,
                            playlistOrigin = playlistName
                        )
                    )
                    currentName = null
                    currentLogo = null
                    currentGroup = "General"
                }
            }
        }
        return channels
    }

    private fun extractAttr(line: String, attr: String): String? {
        val regex = Regex("$attr=\"([^\"]*)\"")
        return regex.find(line)?.groupValues?.get(1)?.ifBlank { null }
    }
}
