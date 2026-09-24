package com.sceventhunters.sceventfishing.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.sceventhunters.sceventfishing.data.model.AppMode
import com.sceventhunters.sceventfishing.ui.theme.ThemeMode

const val PREFS_NAME = "prefs"
const val KEY_THEME_MODE = "theme_mode"
const val KEY_APP_MODE = "app_mode"
const val KEY_COPY_WITHOUT_LINK = "copy_no_link"
const val KEY_COMPAT_FOLDER_URI = "compat_folder_uri"
const val KEY_COMPAT_FOLDER_URI_PREFIX = "compat_folder_uri_"
const val KEY_EXPORT_FOLDER_URI = "export_folder_uri"
const val KEY_SCHUNT_PACKAGES = "schunt_packages"
const val KEY_PROCESSED_FILES = "processed"
const val KEY_SELECTED_PACKAGE = "selected"
const val KEY_LANGUAGE = "language"
const val KEY_FILTER_UI_FILES = "filter_ui_files"

fun saveLanguage(context: Context, languageCode: String) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_LANGUAGE, languageCode).apply()
}

fun loadLanguage(context: Context): String? {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_LANGUAGE, null)
}

fun applyStoredLanguage(context: Context) {
    val saved = loadLanguage(context) ?: return
    val current = AppCompatDelegate.getApplicationLocales()
    val currentLang = if (!current.isEmpty) current[0]?.language else null
    if (currentLang != saved) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(saved))
    }
}

fun saveProcessedFiles(context: Context, files: Set<String>) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putStringSet(KEY_PROCESSED_FILES, files).apply()
}

fun loadProcessedFiles(context: Context): Set<String> {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getStringSet(KEY_PROCESSED_FILES, emptySet()) ?: emptySet()
}

fun saveSelectedPackage(context: Context, pkg: String?) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_SELECTED_PACKAGE, pkg).apply()
}

fun loadSelectedPackage(context: Context): String? {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_SELECTED_PACKAGE, null)
}

fun saveCopyWithoutLink(context: Context, value: Boolean) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_COPY_WITHOUT_LINK, value).apply()
}

fun loadCopyWithoutLink(context: Context): Boolean {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_COPY_WITHOUT_LINK, false)
}

fun saveAppMode(context: Context, mode: AppMode) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_APP_MODE, mode.name).apply()
}

fun loadAppMode(context: Context): AppMode {
    val name = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_APP_MODE, AppMode.REGULAR.name)
    return try { AppMode.valueOf(name ?: AppMode.REGULAR.name) } catch (e: Exception) { AppMode.REGULAR }
}

fun loadSchuntPackages(context: Context): Set<String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    if (!prefs.contains(KEY_SCHUNT_PACKAGES)) {
        val defaultSet = setOf("com.supercell.brawlstars")
        saveSchuntPackages(context, defaultSet)
        return defaultSet
    }
    return prefs.getStringSet(KEY_SCHUNT_PACKAGES, emptySet()) ?: emptySet()
}

fun saveSchuntPackages(context: Context, packages: Set<String>) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        .putStringSet(KEY_SCHUNT_PACKAGES, packages).apply()
}

fun addSchuntPackage(context: Context, packageName: String) {
    val current = loadSchuntPackages(context).toMutableSet()
    current.add(packageName)
    saveSchuntPackages(context, current)
}

fun removeSchuntPackage(context: Context, packageName: String) {
    val current = loadSchuntPackages(context).toMutableSet()
    current.remove(packageName)
    saveSchuntPackages(context, current)
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        .remove(getCompatFolderPreferenceKey(packageName)).apply()
}

fun getCompatFolderPreferenceKey(packageName: String): String = "$KEY_COMPAT_FOLDER_URI_PREFIX$packageName"

fun saveCompatFolderUri(context: Context, packageName: String, uri: String?) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(getCompatFolderPreferenceKey(packageName), uri).apply()
}

fun saveCompatFolderUri(context: Context, uri: String?) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_COMPAT_FOLDER_URI, uri).apply()
}

fun loadCompatFolderUri(context: Context, packageName: String): String? {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val specific = prefs.getString(getCompatFolderPreferenceKey(packageName), null)
    if (specific != null) return specific
    return prefs.getString(KEY_COMPAT_FOLDER_URI, null)
}

fun loadCompatFolderUri(context: Context): String? {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_COMPAT_FOLDER_URI, null)
}

fun saveExportFolderUri(context: Context, uri: String?) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_EXPORT_FOLDER_URI, uri).apply()
}

fun loadExportFolderUri(context: Context): String? {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_EXPORT_FOLDER_URI, null)
}

fun loadThemeMode(context: Context): ThemeMode {
    val name = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
    return try { ThemeMode.valueOf(name ?: ThemeMode.SYSTEM.name) } catch (e: Exception) { ThemeMode.SYSTEM }
}

fun saveThemeMode(context: Context, mode: ThemeMode) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_THEME_MODE, mode.name).apply()
}

fun saveFilterUiFiles(context: Context, value: Boolean) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_FILTER_UI_FILES, value).apply()
}

fun loadFilterUiFiles(context: Context): Boolean {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_FILTER_UI_FILES, true)
}
