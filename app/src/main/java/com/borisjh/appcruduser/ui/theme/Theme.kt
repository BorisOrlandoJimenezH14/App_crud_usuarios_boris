package com.borisjh.appcruduser.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BlackBackground = Color(0xFF000000)
val DarkSurface = Color(0xFF121212)
val CardSurface = Color(0xFF1E1E1E)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9E9E9E)
val AccentGray = Color(0xFF333333)
val BorderColor = Color(0xFF2C2C2C)
val ErrorColor = Color(0xFFCF6679)

private val DarkColorScheme = darkColorScheme(
    primary = TextPrimary,
    onPrimary = BlackBackground,
    primaryContainer = CardSurface,
    onPrimaryContainer = TextPrimary,
    background = BlackBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = CardSurface,
    onSurfaceVariant = TextSecondary,
    error = ErrorColor,
    outline = BorderColor
)

@Composable
fun AppCrudUserTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
