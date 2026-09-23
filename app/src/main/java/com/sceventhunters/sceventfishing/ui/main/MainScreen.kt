package com.sceventhunters.sceventfishing.ui.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.sceventhunters.sceventfishing.R
import com.sceventhunters.sceventfishing.SettingsActivity
import com.sceventhunters.sceventfishing.data.model.AppInfo
import com.sceventhunters.sceventfishing.data.model.AppMode
import com.sceventhunters.sceventfishing.data.repository.*
import android.content.pm.ActivityInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.sceventhunters.sceventfishing.ui.settings.friendlyFolderPath
import com.sceventhunters.sceventfishing.ui.theme.SCEventFishingTheme
import com.sceventhunters.sceventfishing.ui.util.LockScreenOrientation
import com.sceventhunters.sceventfishing.ui.util.rememberPreference
import com.sceventhunters.sceventfishing.util.*
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    val brawlstarsPackages = listOf(
        "bsd.suitcase.release",
        "com.supercell.brawlstars",
        // "com.magics.brawl",
        "com.tencent.tmgp.supercell.brawlstars"
    )
    val clashRoyalePackages = listOf(
        "com.supercell.clashroyale",
        "com.tencent.tmgp.supercell.clashroyale"
    )

    var refreshTrigger by remember { mutableStateOf(0) }
    var sessionProcessedFiles by remember { mutableStateOf(loadProcessedFiles(context)) }
    var selectedPackageName by remember { mutableStateOf(loadSelectedPackage(context)) }
    val copyWithoutLink = rememberPreference(context, KEY_COPY_WITHOUT_LINK) { loadCopyWithoutLink(it) }
    val appMode = rememberPreference(context, KEY_APP_MODE) { loadAppMode(it) }
    val compatFolderUri = rememberPreference(context, KEY_COMPAT_FOLDER_URI) { loadCompatFolderUri(it) }
    val exportFolderUri = rememberPreference(context, KEY_EXPORT_FOLDER_URI) { loadExportFolderUri(it) }

    val updateProcessedFiles = remember(context) {
        { newFiles: Set<String> ->
            sessionProcessedFiles = newFiles
            saveProcessedFiles(context, newFiles)
        }
    }

    val updateSelectedPackage = remember(context) {
        { pkg: String? ->
            selectedPackageName = pkg
            saveSelectedPackage(context, pkg)
        }
    }

    val schuntPackages = rememberPreference(context, KEY_SCHUNT_PACKAGES) { loadSchuntPackages(it) }

    val brawlstarsAppList = remember(refreshTrigger) { brawlstarsPackages.map { getAppInfo(packageManager, it) } }
    val installedBrawlstarsApps = remember(brawlstarsAppList, appMode, schuntPackages) {
        val installed = brawlstarsAppList.filter { it.isInstalled }
        if (appMode == AppMode.COMPATIBILITY) {
            installed.filter { schuntPackages.contains(it.packageName) }
        } else {
            installed
        }
    }
    val notInstalledBrawlstarsApps = remember(brawlstarsAppList, appMode, schuntPackages) {
        val notInstalled = brawlstarsAppList.filterNot { it.isInstalled }
        if (appMode == AppMode.COMPATIBILITY) {
            notInstalled.filter { schuntPackages.contains(it.packageName) }
        } else {
            notInstalled
        }
    }

    val clashRoyaleAppList = remember(refreshTrigger) { clashRoyalePackages.map { getAppInfo(packageManager, it) } }
    val installedClashRoyaleApps = remember(clashRoyaleAppList, appMode, schuntPackages) {
        val installed = clashRoyaleAppList.filter { it.isInstalled }
        if (appMode == AppMode.COMPATIBILITY) {
            installed.filter { schuntPackages.contains(it.packageName) }
        } else {
            installed
        }
    }
    val notInstalledClashRoyaleApps = remember(clashRoyaleAppList, appMode, schuntPackages) {
        val notInstalled = clashRoyaleAppList.filterNot { it.isInstalled }
        if (appMode == AppMode.COMPATIBILITY) {
            notInstalled.filter { schuntPackages.contains(it.packageName) }
        } else {
            notInstalled
        }
    }

    val installedApps = installedBrawlstarsApps + installedClashRoyaleApps
    val notInstalledApps = notInstalledBrawlstarsApps + notInstalledClashRoyaleApps

    LaunchedEffect(installedApps) {
        if (installedApps.isNotEmpty()) {
            if (selectedPackageName == null || installedApps.none { it.packageName == selectedPackageName }) {
                updateSelectedPackage(installedApps.first().packageName)
            }
        } else {
            if (selectedPackageName != null) {
                updateSelectedPackage(null)
            }
        }
    }

    val rootAvailable = remember { isRootAvailable() }
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp.dp >= 600.dp

    if (configuration.smallestScreenWidthDp >= 600) {
        LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    if (appMode == AppMode.REGULAR && !rootAvailable) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.root_required_regular),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { context.startActivity(Intent(context, SettingsActivity::class.java)) }) {
                        Text(stringResource(R.string.go_to_settings))
                    }
                }
            }
        }
        return
    }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    stringResource(R.string.app_name),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                if (installedBrawlstarsApps.isNotEmpty()) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Games, contentDescription = null) },
                        label = { Text(stringResource(R.string.brawl_stars)) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                scrollState.animateScrollToItem(1) // Usually index 1 if BS is first
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
                if (installedClashRoyaleApps.isNotEmpty()) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Games, contentDescription = null) },
                        label = { Text(stringResource(R.string.clash_royale)) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                // Index depends on BS size. We might need keys.
                                val targetIndex = if (installedBrawlstarsApps.isNotEmpty()) 2 + installedBrawlstarsApps.size else 1
                                scrollState.animateScrollToItem(targetIndex)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.settings)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text(stringResource(R.string.about)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            val intent = Intent(context, SettingsActivity::class.java).apply {
                                putExtra("scrollToAbout", true)
                            }
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.app_name))
                            Spacer(modifier = Modifier.width(8.dp))
                            val modeLabel = when (appMode) {
                                AppMode.REGULAR -> stringResource(R.string.mode_regular_badge)
                                AppMode.COMPATIBILITY -> stringResource(R.string.mode_compat_badge)
                                AppMode.DEMO -> stringResource(R.string.mode_demo_badge)
                            }
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = modeLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu_description))
                        }
                    },
                    actions = {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_description))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.settings)) },
                                onClick = {
                                    showMenu = false
                                    context.startActivity(Intent(context, SettingsActivity::class.java))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.about)) },
                                onClick = {
                                    showMenu = false
                                    val intent = Intent(context, SettingsActivity::class.java).apply {
                                        putExtra("scrollToAbout", true)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                )
            }
        ) { innerScaffoldPadding ->
            Box(modifier = Modifier.padding(innerScaffoldPadding).fillMaxSize()) {
                if (isTablet) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            state = scrollState,
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .padding(top = 16.dp)
                        ) {
                            item {
                                Column(Modifier.padding(horizontal = 16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(R.string.monitored_apps),
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        FilledTonalButton(onClick = { refreshTrigger++ }) {
                                            Text(stringResource(R.string.refresh_all))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                            if (appMode == AppMode.COMPATIBILITY && installedBrawlstarsApps.isEmpty() && installedClashRoyaleApps.isEmpty()) {
                                item {
                                    ElevatedCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = stringResource(R.string.no_schunt_targets),
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Button(onClick = { context.startActivity(Intent(context, SettingsActivity::class.java)) }) {
                                                Text(stringResource(R.string.add_schunt_target))
                                            }
                                        }
                                    }
                                }
                            }
                            if (installedBrawlstarsApps.isNotEmpty()) {
                                item {
                                    Column(Modifier.padding(horizontal = 16.dp)) {
                                        Text(text = stringResource(R.string.brawl_stars_label), style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                                items(installedBrawlstarsApps) { app ->
                                    AppListItem(
                                        appInfo = app,
                                        isSelected = app.packageName == selectedPackageName,
                                        onAppSelected = { updateSelectedPackage(it.packageName) }
                                    )
                                }
                            }
                            if (installedClashRoyaleApps.isNotEmpty()) {
                                item {
                                    Column(Modifier.padding(horizontal = 16.dp)) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(text = stringResource(R.string.clash_royale_label), style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                                items(installedClashRoyaleApps) { app ->
                                    AppListItem(
                                        appInfo = app,
                                        isSelected = app.packageName == selectedPackageName,
                                        onAppSelected = { updateSelectedPackage(it.packageName) }
                                    )
                                }
                            }
                            if (notInstalledApps.isNotEmpty()) {
                                item {
                                    Column(Modifier.padding(16.dp)) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(text = stringResource(R.string.not_installed), style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                                items(notInstalledApps) { app ->
                                    AppListItem(appInfo = app, isSelected = false, onAppSelected = {})
                                }
                            }
                        }

                        VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        val selectedApp = installedApps.find { it.packageName == selectedPackageName }
                        val selectedCompatUri = selectedApp?.let { app ->
                            rememberPreference(context, getCompatFolderPreferenceKey(app.packageName)) { loadCompatFolderUri(it, app.packageName) }
                        }
                        Scaffold(
                            modifier = Modifier.weight(2f),
                            floatingActionButton = {
                                if (selectedApp != null) {
                                    val eventFiles = getEventFiles(context, appMode, selectedApp.packageName, selectedCompatUri)
                                    Row {
                                        ExtendedFloatingActionButton(
                                            onClick = {
                                                copyUrlsToClipboard(context, selectedApp.packageName, eventFiles, copyWithoutLink)
                                                updateProcessedFiles(sessionProcessedFiles + eventFiles.map { File(it).name }.toSet())
                                            },
                                            icon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                                            text = { Text(stringResource(R.string.copy_all)) }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        FloatingActionButton(
                                            onClick = {
                                                extractFilesToDownloadEvents(context, selectedApp.packageName, eventFiles, exportFolderUri) {
                                                    updateProcessedFiles(sessionProcessedFiles + eventFiles.map { File(it).name }.toSet())
                                                    refreshTrigger++
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.Download, contentDescription = stringResource(R.string.extract_all))
                                        }
                                    }
                                }
                            }
                        ) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                                if (selectedApp != null) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                            .padding(16.dp)
                                    ) {
                                        AppDetail(
                                            appInfo = selectedApp,
                                            isTablet = true,
                                            appMode = appMode,
                                            compatFolderUri = compatFolderUri,
                                            exportFolderUri = exportFolderUri,
                                            refreshTrigger = refreshTrigger,
                                            sessionProcessedFiles = sessionProcessedFiles,
                                            onSessionFilesChanged = updateProcessedFiles,
                                            onRefresh = { refreshTrigger++ },
                                            copyWithoutLink = copyWithoutLink
                                        )
                                    }
                                } else {
                                    EmptyDetailView(modifier = Modifier.fillMaxSize())
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.monitored_apps_headline),
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                FilledTonalButton(onClick = { refreshTrigger++ }) {
                                    Text(stringResource(R.string.refresh_all))
                                }
                            }
                        }

                        if (appMode == AppMode.COMPATIBILITY && installedBrawlstarsApps.isEmpty() && installedClashRoyaleApps.isEmpty()) {
                            item {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(R.string.no_schunt_targets),
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(onClick = { context.startActivity(Intent(context, SettingsActivity::class.java)) }) {
                                            Text(stringResource(R.string.add_schunt_target))
                                        }
                                    }
                                }
                            }
                        }

                        if (installedBrawlstarsApps.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.brawl_stars_label),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            items(installedBrawlstarsApps) { appInfo ->
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                ) {
                                    AppDetail(
                                        appInfo = appInfo,
                                        isTablet = false,
                                        appMode = appMode,
                                        compatFolderUri = compatFolderUri,
                                        exportFolderUri = exportFolderUri,
                                        modifier = Modifier.padding(16.dp),
                                        refreshTrigger = refreshTrigger,
                                        sessionProcessedFiles = sessionProcessedFiles,
                                        onSessionFilesChanged = updateProcessedFiles,
                                        onRefresh = { refreshTrigger++ },
                                        copyWithoutLink = copyWithoutLink
                                    )
                                }
                            }
                        }

                        if (installedClashRoyaleApps.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.clash_royale_label),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            items(installedClashRoyaleApps) { appInfo ->
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                ) {
                                    AppDetail(
                                        appInfo = appInfo,
                                        isTablet = false,
                                        appMode = appMode,
                                        compatFolderUri = compatFolderUri,
                                        exportFolderUri = exportFolderUri,
                                        modifier = Modifier.padding(16.dp),
                                        refreshTrigger = refreshTrigger,
                                        sessionProcessedFiles = sessionProcessedFiles,
                                        onSessionFilesChanged = updateProcessedFiles,
                                        onRefresh = { refreshTrigger++ },
                                        copyWithoutLink = copyWithoutLink
                                    )
                                }
                            }
                        }

                        if (notInstalledApps.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.not_installed),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            items(notInstalledApps) { appInfo ->
                                Text(
                                    text = appInfo.appName,
                                    modifier = Modifier.padding(start = 32.dp, top = 4.dp, bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppListItem(appInfo: AppInfo, isSelected: Boolean, onAppSelected: (AppInfo) -> Unit) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = appInfo.isInstalled) { onAppSelected(appInfo) }
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        appInfo.icon?.let {
            Image(
                painter = rememberDrawablePainter(drawable = it),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = appInfo.appName,
            style = MaterialTheme.typography.titleMedium,
            color = if (appInfo.isInstalled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun AppDetail(
    appInfo: AppInfo,
    isTablet: Boolean,
    appMode: AppMode,
    compatFolderUri: String? = null,
    modifier: Modifier = Modifier,
    exportFolderUri: String? = null,
    refreshTrigger: Int = 0,
    sessionProcessedFiles: Set<String> = emptySet(),
    copyWithoutLink: Boolean = false,
    onSessionFilesChanged: (Set<String>) -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    val effectiveCompatFolderUri = rememberPreference(context, getCompatFolderPreferenceKey(appInfo.packageName)) {
        loadCompatFolderUri(it, appInfo.packageName)
    } ?: compatFolderUri

    val eventFiles = remember(appInfo.packageName, refreshTrigger, appMode, effectiveCompatFolderUri) {
        getEventFiles(context, appMode, appInfo.packageName, effectiveCompatFolderUri)
    }

    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val eventsDir = File(downloadDir, "events")

    val extractedFileNames = remember(appInfo.packageName, refreshTrigger) {
        RootShell.runCommand("ls \"${eventsDir.absolutePath}\" 2>/dev/null").toSet()
    }

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            appInfo.icon?.let {
                Image(
                    painter = rememberDrawablePainter(drawable = it),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = appInfo.appName, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))

            if (!isTablet && eventFiles.isNotEmpty()) {
                                Button(onClick = {
                                    copyUrlsToClipboard(context, appInfo.packageName, eventFiles, copyWithoutLink)
                                    onSessionFilesChanged(sessionProcessedFiles + eventFiles.map { File(it).name }.toSet())
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(stringResource(R.string.copy_all))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                FilledTonalIconButton(onClick = {
                                    extractFilesToDownloadEvents(context, appInfo.packageName, eventFiles, exportFolderUri) {
                                        onSessionFilesChanged(sessionProcessedFiles + eventFiles.map { File(it).name }.toSet())
                                        onRefresh()
                                    }
                                }) {
                                    Icon(Icons.Default.Download, contentDescription = stringResource(R.string.extract_all))
                                }
                            }

            if (appInfo.isInstalled) {
                Spacer(modifier = Modifier.width(8.dp))
                FilledTonalButton(onClick = {
                    val launchIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName)
                    if (launchIntent != null) context.startActivity(launchIntent)
                    else Toast.makeText(context, context.getString(R.string.could_not_launch), Toast.LENGTH_SHORT).show()
                }) {
                    Text(stringResource(R.string.launch))
                }
            }
        }

        if (eventFiles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.event_files_found, eventFiles.size), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(start = 8.dp)) {
                eventFiles.forEach { filePath ->
                    val fileName = if (filePath.startsWith("content://")) {
                        DocumentFile.fromSingleUri(context, Uri.parse(filePath))?.name ?: "unknown"
                    } else {
                        File(filePath).name
                    }
                    val isProcessed = extractedFileNames.contains(fileName) || sessionProcessedFiles.contains(fileName)
                    val itemColor = if (isProcessed) MaterialTheme.colorScheme.primary else Color.Red

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val textToCopy = if (copyWithoutLink) fileName else getBaseUrlForPackage(appInfo.packageName) + fileName
                                clipboardManager.setPrimaryClip(ClipData.newPlainText(fileName, textToCopy))
                                onSessionFilesChanged(sessionProcessedFiles + fileName)
                                Toast.makeText(context, context.getString(R.string.copied_toast, textToCopy), Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp), tint = itemColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = fileName, color = itemColor, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = stringResource(R.string.extract),
                            modifier = Modifier.size(20.dp).clickable {
                                extractFilesToDownloadEvents(context, appInfo.packageName, listOf(filePath), exportFolderUri) {
                                    onSessionFilesChanged(sessionProcessedFiles + fileName)
                                    onRefresh()
                                }
                            },
                            tint = itemColor
                        )
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.no_event_files), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 16.dp))
        }
    }
}

@Composable
fun EmptyDetailView(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(R.string.no_app_selected), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.select_app_hint),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getAppInfo(packageManager: PackageManager, packageName: String): AppInfo {
    return try {
        val appInfo = packageManager.getApplicationInfo(packageName, 0)
        AppInfo(
            appName = packageManager.getApplicationLabel(appInfo).toString(),
            packageName = packageName,
            icon = packageManager.getApplicationIcon(appInfo),
            isInstalled = true
        )
    } catch (e: Exception) {
        AppInfo(appName = packageName, packageName = packageName, icon = null, isInstalled = false)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SCEventFishingTheme { AppContent() }
}
