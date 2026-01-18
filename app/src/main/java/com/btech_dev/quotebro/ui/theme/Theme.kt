package com.btech_dev.quotebro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor, // Lighter blue for dark mode
    secondary = Color(0xFF5A8DE8),
    tertiary = Pink80,
    background = Color(0xFF121212), // Dark background
    surface = Color(0xFF1E1E1E), // Dark surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFE0E0E0), // Light text on dark bg
    onSurface = Color(0xFFE0E0E0),
    error = Color(0xFFCF6679),
    surfaceVariant = Color(0xFF2C2C2C) // Slightly lighter surface for cards
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor, // 0xff1d4ed8
    secondary = SecondaryColor, // 0xff4079f1
    tertiary = Pink40,
    background = BackgroundWhite, // 0xFFF8F9FA
    surface = SurfaceWhite, // 0xFFFFFFFF
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextDarkSlate, // 0xFF1E293B
    onSurface = TextDarkSlate,
    error = ErrorRed,
    surfaceVariant = BackgroundWhite // 0xFFF1F3F5
)

@Composable
fun QuoteBroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) DarkColorScheme
        else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography(fontScale),
        content = content
    )
}