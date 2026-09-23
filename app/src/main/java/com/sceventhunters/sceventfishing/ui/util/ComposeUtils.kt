package com.sceventhunters.sceventfishing.ui.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
    val currentLoader by rememberUpdatedState(loader)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(context, key, lifecycleOwner) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key || changedKey == null) {
                value = currentLoader(context)
            }
        }

        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                value = currentLoader(context)
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(prefListener)
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(prefListener)
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return value
}
