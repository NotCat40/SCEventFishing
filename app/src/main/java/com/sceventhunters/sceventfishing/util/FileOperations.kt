package com.sceventhunters.sceventfishing.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import com.sceventhunters.sceventfishing.R
import com.sceventhunters.sceventfishing.data.model.AppMode
import java.io.File

fun getBaseUrlForPackage(packageName: String): String {
    return when (packageName) {
        "com.tencent.tmgp.supercell.brawlstars" -> "https://event-assets.tencent-cloud.com/"
        "com.supercell.clashroyale" -> "https://event-assets.clashroyale.com/"
        "com.tencent.tmgp.supercell.clashroyale" -> "https://event-assets.tencent-cloud.com/"
        else -> "https://event-assets.brawlstars.com/"
    }
}

fun getFileName(context: Context, filePath: String): String {
    return if (filePath.startsWith("content://")) {
        try {
            val uri = Uri.parse(filePath)
            uri.lastPathSegment?.substringAfterLast('/') ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    } else {
        File(filePath).name
    }
}

fun copyUrlsToClipboard(
    context: Context,
    packageName: String,
    eventFiles: List<String>,
    copyWithoutLink: Boolean = false
) {
    if (eventFiles.isEmpty()) return
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val baseUrl = getBaseUrlForPackage(packageName)
    val allText = eventFiles.joinToString("\n") { filePath ->
        val fileName = getFileName(context, filePath)
        if (copyWithoutLink) fileName else baseUrl + fileName
    }
    clipboardManager.setPrimaryClip(ClipData.newPlainText("Event Files", allText))
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, context.getString(R.string.copied_items_toast, eventFiles.size), Toast.LENGTH_SHORT).show()
    }
}

fun extractFilesToDownloadEvents(
    context: Context,
    packageName: String,
    eventFiles: List<String>,
    exportFolderUri: String? = null,
    onComplete: () -> Unit
) {
    if (eventFiles.isEmpty()) return

    val destDir: DocumentFile? = if (exportFolderUri != null) {
        DocumentFile.fromTreeUri(context, Uri.parse(exportFolderUri))
    } else {
        val eventsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "events")
        if (!eventsDir.exists()) eventsDir.mkdirs()
        DocumentFile.fromFile(eventsDir)
    }

    if (destDir == null || !destDir.exists()) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, "Destination folder not available", Toast.LENGTH_SHORT).show()
        }
        return
    }

    var successCount = 0
    eventFiles.forEach { filePath ->
        val fileName = getFileName(context, filePath)

        val destFile = destDir.findFile(fileName) ?: destDir.createFile("*/*", fileName)
        if (destFile == null) return@forEach

        try {
            if (filePath.startsWith("content://")) {
                context.contentResolver.openInputStream(Uri.parse(filePath))?.use { input ->
                    context.contentResolver.openOutputStream(destFile.uri)?.use { output ->
                        input.copyTo(output)
                    }
                }
                successCount++
            } else {
                val tempFile = File(context.cacheDir, fileName)
                RootShell.runCommand("cp \"$filePath\" \"${tempFile.absolutePath}\" && chmod 666 \"${tempFile.absolutePath}\"")
                if (tempFile.exists()) {
                    tempFile.inputStream().use { input ->
                        context.contentResolver.openOutputStream(destFile.uri)?.use { output ->
                            input.copyTo(output)
                        }
                    }
                    tempFile.delete()
                    successCount++
                }
            }
        } catch (e: Exception) {
            // Log error
        }
    }
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, context.getString(R.string.extracted_files_toast, successCount), Toast.LENGTH_SHORT).show()
        clearEventFilesCache()
        onComplete()
    }
}

private val eventFilesCache = mutableMapOf<String, List<String>>()

fun clearEventFilesCache() {
    eventFilesCache.clear()
}

fun getEventFiles(
    context: Context,
    mode: AppMode,
    packageName: String,
    compatFolderUri: String? = null,
    extensions: List<String> = listOf(".sc", ".jpg", ".png")
): List<String> {
    val cacheKey = "$mode-$packageName-$compatFolderUri"
    return eventFilesCache.getOrPut(cacheKey) {
        when (mode) {
            AppMode.REGULAR -> {
                val path = "/data/data/$packageName/cache/events/"
                val res = RootShell.runCommand("[ -d \"$path\" ] && find $path -type f \\( ${extensions.joinToString(" -o ") { "-name '*$it'" }} \\) 2>/dev/null")
                res.filter { it.startsWith(path) }.distinctBy { it.substringAfterLast("/") }.sorted()
            }
            AppMode.COMPATIBILITY -> {
                val folderUri = compatFolderUri ?: com.sceventhunters.sceventfishing.data.repository.loadCompatFolderUri(context, packageName)
                if (folderUri == null) return@getOrPut emptyList()
                try {
                    val treeUri = Uri.parse(folderUri)
                    val documentFile = DocumentFile.fromTreeUri(context, treeUri)
                    documentFile?.listFiles()?.filter { file ->
                        file.isFile && extensions.any { ext -> file.name?.endsWith(ext) == true }
                    }?.map { it.uri.toString() } ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                }
            }
            AppMode.DEMO -> {
                listOf(
                    "/demo/events/event_1.sc",
                    "/demo/events/event_2.sc",
                    "/demo/events/background.jpg",
                    "/demo/events/icon.png"
                )
            }
        }
    }
}
