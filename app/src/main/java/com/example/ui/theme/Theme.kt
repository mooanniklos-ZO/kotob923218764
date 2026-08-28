package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldDarkPrimary,
    onPrimary = EmeraldDarkOnPrimary,
    primaryContainer = EmeraldDarkPrimaryContainer,
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = EmeraldDarkSecondary,
    onSecondary = EmeraldDarkOnPrimary,
    tertiary = DarkGoldAccent,
    background = EmeraldDarkBackground,
    surface = EmeraldDarkSurface,
    surfaceVariant = EmeraldDarkSurfaceVariant,
    onBackground = DarkTextLight,
    onSurface = DarkTextLight,
    onSurfaceVariant = DarkTextMuted,
    outline = Color(0xFF3B5E47)
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = Color(0xFF0D3311),
    secondary = EmeraldSecondary,
    onSecondary = Color.White,
    secondaryContainer = EmeraldSecondaryContainer,
    tertiary = HarvestGold,
    background = WarmParchment,
    surface = WarmSurface,
    surfaceVariant = WarmSurfaceVariant,
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextMuted,
    outline = Color(0xFFBCCBBF)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
