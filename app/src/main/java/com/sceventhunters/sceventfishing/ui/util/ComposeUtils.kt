package com.sceventhunters.sceventfishing.ui.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.sceventhunters.sceventfishing.data.repository.PREFS_NAME

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    LaunchedEffect(orientation) {
        val activity = context.findActivity()
        activity?.requestedOrientation = orientation
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun <T> rememberPreference(context: Context, key: String, loader: (Context) -> T): T {
    var value by remember(context, key) { mutableStateOf(loader(context)) }
    DisposableEffect(context, key) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                value = loader(context)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    return value
}
