package com.tulaza.tvlive.player

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Reproduce un canal de YouTube en vivo embebido usando el reproductor
 * oficial de YouTube (youtube.com/embed/...), respetando los términos de
 * servicio de YouTube. No descarga ni redistribuye el video.
 *
 * `youtubeChannelId` es el channelId del canal (ej: UCxxxx). Usamos el modo
 * "live_stream?channel=" que YouTube resuelve automáticamente a la
 * transmisión en vivo activa del canal, sin necesidad de conocer el videoId.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YoutubeLivePlayer(youtubeChannelId: String, modifier: Modifier = Modifier) {
    val embedUrl = "https://www.youtube.com/embed/live_stream?channel=$youtubeChannelId&autoplay=1&playsinline=1"

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                webChromeClient = WebChromeClient()
                loadUrl(embedUrl)
            }
        },
        update = { webView -> webView.loadUrl(embedUrl) }
    )
}
