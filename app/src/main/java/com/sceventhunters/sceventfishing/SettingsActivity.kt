package com.sceventhunters.sceventfishing

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.LocalContext
import com.sceventhunters.sceventfishing.data.repository.KEY_THEME_MODE
import com.sceventhunters.sceventfishing.data.repository.applyStoredLanguage
import com.sceventhunters.sceventfishing.data.repository.loadThemeMode
import com.sceventhunters.sceventfishing.data.repository.saveCompatFolderUri
import com.sceventhunters.sceventfishing.data.repository.saveExportFolderUri
import com.sceventhunters.sceventfishing.ui.settings.SettingsScreen
import com.sceventhunters.sceventfishing.ui.theme.SCEventFishingTheme
import com.sceventhunters.sceventfishing.ui.util.rememberPreference

class SettingsActivity : AppCompatActivity() {
    private val openCompatFolderLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let {
            contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            saveCompatFolderUri(this, it.toString())
        }
    }

    private val openExportFolderLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let {
            contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            saveExportFolderUri(this, it.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyStoredLanguage(this)
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val themeMode = rememberPreference(context, KEY_THEME_MODE) { loadThemeMode(it) }
            val scrollToAbout = intent.getBooleanExtra("scrollToAbout", false)

            SCEventFishingTheme(themeMode = themeMode) {
                SettingsScreen(
                    themeMode = themeMode,
                    scrollToAbout = scrollToAbout,
                    onBack = { finish() },
                    onSelectCompatFolder = { openCompatFolderLauncher.launch(null) },
                    onSelectExportFolder = { openExportFolderLauncher.launch(null) }
                )
            }
        }
    }
}
