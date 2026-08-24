package com.tulaza.tvlive.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

private val DarkColors = darkColorScheme(
    primary = PrimaryIndigoLight,
    secondary = AccentCoral,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark
)

private val LightColors = lightColorScheme(
    primary = PrimaryIndigo,
    secondary = AccentCoral,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight
)

val TVLiveTypography = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, fontFamily = FontFamily.Default),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, fontFamily = FontFamily.Default),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, fontFamily = FontFamily.Default),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, fontFamily = FontFamily.Default),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, fontFamily = FontFamily.Default)
)

@Composable
fun TVLiveTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = TVLiveTypography,
        content = content
    )
}
