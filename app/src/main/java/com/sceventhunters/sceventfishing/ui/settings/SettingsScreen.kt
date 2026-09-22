package com.sceventhunters.sceventfishing.ui.settings

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.provider.DocumentsContract
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.sceventhunters.sceventfishing.R
import com.sceventhunters.sceventfishing.data.model.AppMode
import com.sceventhunters.sceventfishing.data.repository.*
import com.sceventhunters.sceventfishing.ui.theme.ThemeMode
import com.sceventhunters.sceventfishing.ui.util.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    scrollToAbout: Boolean = false,
    onBack: () -> Unit,
    onSelectCompatFolder: () -> Unit,
    onSelectExportFolder: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollToAbout) {
        if (scrollToAbout) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
    var currentLanguage by remember {
        val saved = loadLanguage(context)
        val locales = AppCompatDelegate.getApplicationLocales()
        val fallback = if (!locales.isEmpty) locales[0]?.language else context.resources.configuration.locales[0].language
        mutableStateOf(saved ?: fallback ?: "en")
    }
    var themeExpanded by remember { mutableStateOf(false) }
    var langExpanded by remember { mutableStateOf(false) }
    val copyWithoutLink = rememberPreference(context, KEY_COPY_WITHOUT_LINK) { loadCopyWithoutLink(it) }
    val appMode = rememberPreference(context, KEY_APP_MODE) { loadAppMode(it) }
    val compatFolderUri = rememberPreference(context, KEY_COMPAT_FOLDER_URI) { loadCompatFolderUri(it) }
    val exportFolderUri = rememberPreference(context, KEY_EXPORT_FOLDER_URI) { loadExportFolderUri(it) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_about)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // --- Settings Group ---
            Text(
                text = stringResource(R.string.general_settings),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.copy_without_link), style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = stringResource(R.string.copy_without_link_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = copyWithoutLink,
                            onCheckedChange = { saveCopyWithoutLink(context, it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column {
                        Text(text = stringResource(R.string.export_folder), style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = exportFolderUri?.let { friendlyFolderPath(it) } ?: stringResource(R.string.no_folder_selected),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onSelectExportFolder,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.select_folder))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Appearance Group ---
            Text(
                text = stringResource(R.string.visuals),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = themeExpanded,
                        onExpandedChange = { themeExpanded = !themeExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val themeText = when(themeMode) {
                            ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                            ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                            ThemeMode.DARK -> stringResource(R.string.theme_dark)
                        }
                        OutlinedTextField(
                            value = themeText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.select_theme)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = themeExpanded,
                            onDismissRequest = { themeExpanded = false }
                        ) {
                            ThemeMode.values().forEach { mode ->
                                DropdownMenuItem(
                                    text = {
                                        val label = when(mode) {
                                            ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                                            ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                                            ThemeMode.DARK -> stringResource(R.string.theme_dark)
                                        }
                                        Text(label)
                                    },
                                    onClick = {
                                        saveThemeMode(context, mode)
                                        themeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = langExpanded,
                        onExpandedChange = { langExpanded = !langExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val langText = when(currentLanguage) {
                            "ru" -> stringResource(R.string.language_ru)
                            else -> stringResource(R.string.language_en)
                        }
                        OutlinedTextField(
                            value = langText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.select_language)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = langExpanded,
                            onDismissRequest = { langExpanded = false }
                        ) {
                            listOf("en", "ru").forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        val label = when(lang) {
                                            "ru" -> stringResource(R.string.language_ru)
                                            else -> stringResource(R.string.language_en)
                                        }
                                        Text(label)
                                    },
                                    onClick = {
                                        currentLanguage = lang
                                        saveLanguage(context, lang)
                                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
                                        AppCompatDelegate.setApplicationLocales(appLocale)
                                        langExpanded = false
                                        context.findActivity()?.recreate()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- App Mode Group ---
            Text(
                text = stringResource(R.string.operation_mode),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = appMode.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.app_mode_label)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            AppMode.values().forEach { mode ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(mode.name)
                                            val desc = when(mode) {
                                                AppMode.REGULAR -> stringResource(R.string.mode_regular_desc)
                                                AppMode.COMPATIBILITY -> stringResource(R.string.mode_compat_desc)
                                                AppMode.DEMO -> stringResource(R.string.mode_demo_desc)
                                            }
                                            Text(desc, style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        saveAppMode(context, mode)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (appMode == AppMode.COMPATIBILITY) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column {
                            Text(text = stringResource(R.string.schunt_folder), style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = compatFolderUri?.let { friendlyFolderPath(it) } ?: stringResource(R.string.no_folder_selected),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onSelectCompatFolder,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.select_folder))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- About Group ---
            Text(
                text = stringResource(R.string.about),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    AboutItem(stringResource(R.string.about_app_name), stringResource(R.string.app_name))
                    AboutItem(stringResource(R.string.about_author), stringResource(R.string.author_name))
                    AboutItem(stringResource(R.string.about_description), stringResource(R.string.app_description))
                }
            }
        }
    }
}

private fun friendlyFolderPath(uriString: String): String {
    return try {
        val docId = DocumentsContract.getTreeDocumentId(Uri.parse(uriString))
        val path = docId.substringAfter(':', missingDelimiterValue = docId)
        Uri.decode(path).removePrefix("/storage/emulated/0/")
    } catch (e: Exception) {
        uriString
    }
}

@Composable
fun AboutItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
