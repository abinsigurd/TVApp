package com.alvinwijaya.tvapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Indigo40,
    onPrimary = White,
    primaryContainer = Indigo90,
    onPrimaryContainer = Indigo20,

    secondary = Teal40,
    onSecondary = White,
    secondaryContainer = Teal90,
    onSecondaryContainer = Teal20,

    tertiary = Amber40,
    onTertiary = White,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber20,

    background = Slate50,
    onBackground = Slate900,

    surface = White,
    onSurface = Slate900,

    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,

    outline = Slate300
)

private val DarkColorScheme = darkColorScheme(
    primary = Indigo80,
    onPrimary = Indigo20,
    primaryContainer = Indigo30,
    onPrimaryContainer = Indigo90,

    secondary = Teal80,
    onSecondary = Teal20,
    secondaryContainer = Teal30,
    onSecondaryContainer = Teal90,

    tertiary = Amber80,
    onTertiary = Amber20,
    tertiaryContainer = Amber30,
    onTertiaryContainer = Amber90,

    background = Slate950,
    onBackground = Slate100,

    surface = Slate900,
    onSurface = Slate100,

    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300,

    outline = Slate600
)

@Composable
fun TVAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}