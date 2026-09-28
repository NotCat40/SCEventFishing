package com.sceventhunters.sceventfishing.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class ColorSchemeMode {
    GREEN_ROOT, SCHUNT, PURPLE_SUNSET, CRONA_API, NATIVE
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

private val GreenDarkComponentColors = ComponentColors(
    rawFile = Color(0xFFFF6B6B),
    rawFileIcon = Color(0xFFFF6B6B),
    processedFile = Green80,
    processedFileIcon = Green80,
    badgeBackground = Green30,
    badgeContent = Green90,
    selectedAppBackground = Green30,
    uninstalledAppText = GreenGrey60,
    sectionHeader = Green80
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

private val GreenLightComponentColors = ComponentColors(
    rawFile = Color(0xFFD32F2F),
    rawFileIcon = Color(0xFFD32F2F),
    processedFile = Green40,
    processedFileIcon = Green40,
    badgeBackground = Green90,
    badgeContent = Green10,
    selectedAppBackground = Green90,
    uninstalledAppText = GreenGrey50,
    sectionHeader = Green40
)

private val SchuntDarkColorScheme = darkColorScheme(
    primary = SchuntOrange80,
    onPrimary = Color(0xFF4A2200),
    primaryContainer = Color(0xFF6B3300),
    onPrimaryContainer = SchuntOrange90,
    secondary = SchuntSlate80,
    onSecondary = Color(0xFF243142),
    secondaryContainer = SchuntSlate30,
    onSecondaryContainer = SchuntSlate90,
    tertiary = Color(0xFFFFCC80),
    onTertiary = Color(0xFF432C00),
    tertiaryContainer = Color(0xFF604100),
    onTertiaryContainer = Color(0xFFFFE0B2),
    background = SchuntDarkBg,
    onBackground = Color(0xFFE2E6EE),
    surface = SchuntDarkBg,
    onSurface = Color(0xFFE2E6EE),
    surfaceVariant = SchuntDarkSurface,
    onSurfaceVariant = SchuntSlate80,
    outline = Color(0xFF8A97A8),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
)

private val SchuntDarkComponentColors = ComponentColors(
    rawFile = Color(0xFFFF7043),
    rawFileIcon = Color(0xFFFF7043),
    processedFile = SchuntOrange80,
    processedFileIcon = SchuntOrange80,
    badgeBackground = Color(0xFF6B3300),
    badgeContent = SchuntOrange90,
    selectedAppBackground = Color(0xFF6B3300),
    uninstalledAppText = SchuntSlate80.copy(alpha = 0.6f),
    sectionHeader = SchuntOrange80
)

private val SchuntLightColorScheme = lightColorScheme(
    primary = SchuntOrange40,
    onPrimary = Color.White,
    primaryContainer = SchuntOrange90,
    onPrimaryContainer = SchuntOrange10,
    secondary = SchuntSlate40,
    onSecondary = Color.White,
    secondaryContainer = SchuntSlate90,
    onSecondaryContainer = Color(0xFF091829),
    tertiary = Color(0xFF795900),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDF9E),
    onTertiaryContainer = Color(0xFF261A00),
    background = Color(0xFFFAF8F5),
    onBackground = Color(0xFF151922),
    surface = Color(0xFFFAF8F5),
    onSurface = Color(0xFF151922),
    surfaceVariant = Color(0xFFE8ECEF),
    onSurfaceVariant = Color(0xFF4C5A6E),
    outline = Color(0xFF778596),
    error = Color(0xFFB3261E),
    onError = Color.White,
)

private val SchuntLightComponentColors = ComponentColors(
    rawFile = Color(0xFFE64A19),
    rawFileIcon = Color(0xFFE64A19),
    processedFile = SchuntOrange40,
    processedFileIcon = SchuntOrange40,
    badgeBackground = SchuntOrange90,
    badgeContent = SchuntOrange10,
    selectedAppBackground = SchuntOrange90,
    uninstalledAppText = SchuntSlate40.copy(alpha = 0.6f),
    sectionHeader = SchuntOrange40
)

private val PurpleDarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val PurpleDarkComponentColors = ComponentColors(
    rawFile = Color(0xFFFF5252),
    rawFileIcon = Color(0xFFFF5252),
    processedFile = Purple80,
    processedFileIcon = Purple80,
    badgeBackground = PurpleGrey40,
    badgeContent = Purple80,
    selectedAppBackground = PurpleGrey40,
    uninstalledAppText = PurpleGrey80.copy(alpha = 0.6f),
    sectionHeader = Purple80
)

private val PurpleLightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val PurpleLightComponentColors = ComponentColors(
    rawFile = Color(0xFFD32F2F),
    rawFileIcon = Color(0xFFD32F2F),
    processedFile = Purple40,
    processedFileIcon = Purple40,
    badgeBackground = PurpleGrey80,
    badgeContent = Purple40,
    selectedAppBackground = PurpleGrey80,
    uninstalledAppText = PurpleGrey40.copy(alpha = 0.6f),
    sectionHeader = Purple40
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

private val PurpleSunsetDarkComponentColors = ComponentColors(
    rawFile = Color(0xFFFF5252),
    rawFileIcon = Color(0xFFFF5252),
    processedFile = SunsetPurple80,
    processedFileIcon = SunsetPurple80,
    badgeBackground = SunsetPurple30,
    badgeContent = SunsetPurple90,
    selectedAppBackground = SunsetPurple30,
    uninstalledAppText = SunsetPurpleGrey60,
    sectionHeader = SunsetPurple80
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

private val PurpleSunsetLightComponentColors = ComponentColors(
    rawFile = Color(0xFFC62828),
    rawFileIcon = Color(0xFFC62828),
    processedFile = SunsetPurple40,
    processedFileIcon = SunsetPurple40,
    badgeBackground = SunsetPurple90,
    badgeContent = SunsetPurple10,
    selectedAppBackground = SunsetPurple90,
    uninstalledAppText = SunsetPurpleGrey50,
    sectionHeader = SunsetPurple40
)

private fun getDynamicComponentColors(colorScheme: ColorScheme): ComponentColors {
    return ComponentColors(
        rawFile = colorScheme.error,
        rawFileIcon = colorScheme.error,
        processedFile = colorScheme.primary,
        processedFileIcon = colorScheme.primary,
        badgeBackground = colorScheme.primaryContainer,
        badgeContent = colorScheme.onPrimaryContainer,
        selectedAppBackground = colorScheme.primaryContainer,
        uninstalledAppText = colorScheme.onSurface.copy(alpha = 0.6f),
        sectionHeader = colorScheme.primary
    )
}

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

    val (colorScheme, componentColors) = when {
        (colorSchemeMode == ColorSchemeMode.NATIVE || dynamicColor) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val scheme = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            scheme to getDynamicComponentColors(scheme)
        }
        colorSchemeMode == ColorSchemeMode.SCHUNT -> {
            val scheme = if (darkTheme) SchuntDarkColorScheme else SchuntLightColorScheme
            val comp = if (darkTheme) SchuntDarkComponentColors else SchuntLightComponentColors
            scheme to comp
        }
        colorSchemeMode == ColorSchemeMode.CRONA_API -> {
            val scheme = if (darkTheme) PurpleDarkColorScheme else PurpleLightColorScheme
            val comp = if (darkTheme) PurpleDarkComponentColors else PurpleLightComponentColors
            scheme to comp
        }
        colorSchemeMode == ColorSchemeMode.PURPLE_SUNSET -> {
            val scheme = if (darkTheme) PurpleSunsetDarkColorScheme else PurpleSunsetLightColorScheme
            val comp = if (darkTheme) PurpleSunsetDarkComponentColors else PurpleSunsetLightComponentColors
            scheme to comp
        }
        else -> {
            val scheme = if (darkTheme) GreenDarkColorScheme else GreenLightColorScheme
            val comp = if (darkTheme) GreenDarkComponentColors else GreenLightComponentColors
            scheme to comp
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

    CompositionLocalProvider(LocalComponentColors provides componentColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
