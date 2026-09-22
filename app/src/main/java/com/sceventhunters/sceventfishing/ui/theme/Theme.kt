package com.sceventhunters.sceventfishing.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    secondary = GreenGrey80,
    onSecondary = GreenGrey20,
    secondaryContainer = GreenGrey30,
    onSecondaryContainer = GreenGrey90,
    tertiary = LightGreen80,
    onTertiary = LightGreen20,
    tertiaryContainer = LightGreen30,
    onTertiaryContainer = LightGreen90,
    background = DarkGreen10,
    onBackground = Green90,
    surface = DarkGreen10,
    onSurface = Green90,
    surfaceVariant = GreenGrey30,
    onSurfaceVariant = GreenGrey80,
    outline = GreenGrey60,
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Green100,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = GreenGrey40,
    onSecondary = GreenGrey100,
    secondaryContainer = GreenGrey90,
    onSecondaryContainer = GreenGrey10,
    tertiary = LightGreen40,
    onTertiary = LightGreen100,
    tertiaryContainer = LightGreen90,
    onTertiaryContainer = LightGreen10,
    background = Green99,
    onBackground = Green10,
    surface = Green99,
    onSurface = Green10,
    surfaceVariant = GreenGrey90,
    onSurfaceVariant = GreenGrey30,
    outline = GreenGrey50,
)

@Composable
fun SCEventFishingTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}