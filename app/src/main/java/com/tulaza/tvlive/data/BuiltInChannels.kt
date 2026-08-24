package com.tulaza.tvlive.data

/**
 * Canales de YouTube en vivo integrados por defecto.
 *
 * IMPORTANTE: reemplazá los "source" (video ID o handle) por los IDs reales
 * y actuales de los canales oficiales que quieras mostrar, verificando vos
 * mismo que cada canal permita la reproducción embebida. Los IDs de video
 * "en vivo" de YouTube cambian con cada transmisión, así que en producción
 * conviene resolverlos dinámicamente contra la API de YouTube (YouTube Data
 * API v3, endpoint search con eventType=live) usando el channelId de cada
 * emisora, en vez de hardcodear un videoId fijo.
 */
object BuiltInChannels {

    val defaultChannels: List<Channel> = listOf(
        Channel(
            id = "dw-news",
            name = "DW News",
            category = "Internacional",
            country = "Alemania",
            sourceType = SourceType.YOUTUBE_LIVE,
            source = "UCknLrEdhRCp1aegoMqRaCZg" // channelId de DW News
        ),
        Channel(
            id = "france24-es",
            name = "France 24 Español",
            category = "Internacional",
            country = "Francia",
            sourceType = SourceType.YOUTUBE_LIVE,
            source = "UCw0O2SVDp6mA7bAdENFTNZg"
        ),
        Channel(
            id = "nasa",
            name = "NASA Live",
            category = "Ciencia",
            country = "EE.UU.",
            sourceType = SourceType.YOUTUBE_LIVE,
            source = "UCLA_DiR1FfKNvjuUpBHmylQ"
        ),
        // --- Canales de Argentina y países limítrofes ---
        // channelId verificados contra el canal oficial de YouTube de cada
        // emisora (agosto 2026). Igual te recomiendo confirmarlos vos antes
        // de publicar la app, porque un canal puede cambiar su handle/ID.
        Channel(
            id = "canal26-ar",
            name = "Canal 26",
            category = "Noticias",
            country = "Argentina",
            sourceType = SourceType.YOUTUBE_LIVE,
            source = "UCrpMfcQNog595v5gAS-oUsQ"
        ),
        Channel(
            id = "tn-ar",
            name = "TN - Todo Noticias",
            category = "Noticias",
            country = "Argentina",
            sourceType = SourceType.YOUTUBE_LIVE,
            source = "UCj6PcyLvpnIRT_2W_mwa9Aw"
        ),
        Channel(
            id = "c5n-ar",
            name = "C5N",
            category = "Noticias",
            country = "Argentina",
            sourceType = SourceType.YOUTUBE_LIVE,
            source = "UCFgk2Q2mVO1BklRQhSv6p0w"
        ),
        Channel(
            id = "24horas-cl",
            name = "24 Horas TVN",
            category = "Noticias",
            country = "Chile",
            sourceType = SourceType.YOUTUBE_LIVE,
            source = "UCTXNz3gjAypWp3EhlIATEJQ"
        ),
        Channel(
            id = "globonews-br",
            name = "GloboNews",
            category = "Noticias",
            country = "Brasil",
            sourceType = SourceType.YOUTUBE_LIVE,
            source = "REEMPLAZAR_CHANNEL_ID" // no se pudo confirmar con certeza el channelId oficial; verificalo en youtube.com/@globonews antes de usarlo
        )
    )
}
