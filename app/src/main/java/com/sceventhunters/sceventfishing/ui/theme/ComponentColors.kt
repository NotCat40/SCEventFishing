package com.sceventhunters.sceventfishing.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Component-specific semantic colors for custom UI elements.
 */
@Immutable
data class ComponentColors(
    val rawFile: Color,
    val rawFileIcon: Color = rawFile,
    val processedFile: Color,
    val processedFileIcon: Color = processedFile,
    val badgeBackground: Color,
    val badgeContent: Color,
    val selectedAppBackground: Color,
    val uninstalledAppText: Color,
    val sectionHeader: Color
)

val LocalComponentColors = staticCompositionLocalOf {
    ComponentColors(
        rawFile = Color.Red,
        rawFileIcon = Color.Red,
        processedFile = Color.Green,
        processedFileIcon = Color.Green,
        badgeBackground = Color.Gray,
        badgeContent = Color.White,
        selectedAppBackground = Color.LightGray,
        uninstalledAppText = Color.Gray,
        sectionHeader = Color.Black
    )
}

val MaterialTheme.componentColors: ComponentColors
    @Composable
    @ReadOnlyComposable
    get() = LocalComponentColors.current
