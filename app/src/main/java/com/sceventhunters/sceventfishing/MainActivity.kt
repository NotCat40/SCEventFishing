package com.sceventhunters.sceventfishing

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.LocalContext
import com.sceventhunters.sceventfishing.data.repository.KEY_THEME_MODE
import com.sceventhunters.sceventfishing.data.repository.applyStoredLanguage
import com.sceventhunters.sceventfishing.data.repository.loadThemeMode
import com.sceventhunters.sceventfishing.ui.main.AppContent
import com.sceventhunters.sceventfishing.ui.theme.SCEventFishingTheme
import com.sceventhunters.sceventfishing.ui.util.rememberPreference
import com.sceventhunters.sceventfishing.util.RootShell

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        applyStoredLanguage(this)
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val themeMode = rememberPreference(context, KEY_THEME_MODE) { loadThemeMode(it) }

            SCEventFishingTheme(themeMode = themeMode) {
                AppContent()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RootShell.stop()
    }
}
