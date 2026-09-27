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

import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class ColorSchemeMode {
    GREEN_ROOT, PURPLE_SUNSET, CRONA_API
}

private val GreenDarkColorScheme = darkColorScheme(
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

private val GreenLightColorScheme = lightColorScheme(
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

private val PurpleDarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val PurpleLightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val PurpleSunsetDarkColorScheme = darkColorScheme(
    primary = SunsetPurple80,
    onPrimary = SunsetPurple20,
    primaryContainer = SunsetPurple30,
    onPrimaryContainer = SunsetPurple90,
    secondary = SunsetPurpleGrey80,
    onSecondary = SunsetPurpleGrey20,
    secondaryContainer = SunsetPurpleGrey30,
    onSecondaryContainer = SunsetPurpleGrey90,
    tertiary = SunsetLightPurple80,
    onTertiary = SunsetLightPurple20,
    tertiaryContainer = SunsetLightPurple30,
    onTertiaryContainer = SunsetLightPurple90,
    background = SunsetDarkPurple10,
    onBackground = SunsetPurple90,
    surface = SunsetDarkPurple10,
    onSurface = SunsetPurple90,
    surfaceVariant = SunsetPurpleGrey30,
    onSurfaceVariant = SunsetPurpleGrey80,
    outline = SunsetPurpleGrey60,
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
)

private val PurpleSunsetLightColorScheme = lightColorScheme(
    primary = SunsetPurple40,
    onPrimary = Color.White,
    primaryContainer = SunsetPurple90,
    onPrimaryContainer = SunsetPurple10,
    secondary = SunsetPurpleGrey40,
    onSecondary = Color.White,
    secondaryContainer = SunsetPurpleGrey90,
    onSecondaryContainer = SunsetPurpleGrey10,
    tertiary = SunsetLightPurple40,
    onTertiary = Color.White,
    tertiaryContainer = SunsetLightPurple90,
    onTertiaryContainer = SunsetLightPurple10,
    background = SunsetPurple99,
    onBackground = SunsetPurple10,
    surface = SunsetPurple99,
    onSurface = SunsetPurple10,
    surfaceVariant = SunsetPurpleGrey90,
    onSurfaceVariant = SunsetPurpleGrey30,
    outline = SunsetPurpleGrey50,
    error = Color(0xFFB3261E),
    onError = Color.White,
)

@Composable
fun SCEventFishingTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    colorSchemeMode: ColorSchemeMode = ColorSchemeMode.GREEN_ROOT,
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
        colorSchemeMode == ColorSchemeMode.CRONA_API -> {
            if (darkTheme) PurpleDarkColorScheme else PurpleLightColorScheme
        }
        colorSchemeMode == ColorSchemeMode.PURPLE_SUNSET -> {
            if (darkTheme) PurpleSunsetDarkColorScheme else PurpleSunsetLightColorScheme
        }
        else -> {
            if (darkTheme) GreenDarkColorScheme else GreenLightColorScheme
        }
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