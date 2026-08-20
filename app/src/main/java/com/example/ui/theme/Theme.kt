package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberColorScheme = darkColorScheme(
    primary = CyberPurple,
    secondary = CyberCyan,
    tertiary = CyberBlue,
    background = CyberDarkBg,
    surface = CyberSurface,
    surfaceVariant = CyberSurfaceLight,
    onPrimary = TextPrimary,
    onSecondary = CyberDarkBg,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = CyberBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}

