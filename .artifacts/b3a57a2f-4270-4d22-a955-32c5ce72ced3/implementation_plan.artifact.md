# Implementation Plan: Localization, Language Switching, and Theme Switching

This plan outlines the steps to add Russian localization, language selection, and theme selection to the SCEventFishing app.

## Proposed Changes

### 1. Localization (Russian)
Create a new resource file for Russian translations and update the default strings to include theme and language labels.

#### [MODIFY] [strings.xml](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/res/values/strings.xml)
- Add string resources for theme names (Light, Dark, System) and language names.

#### [NEW] [strings.xml](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/res/values-ru/strings.xml)
- Add Russian translations for all strings.

### 2. Dependency Update
#### [MODIFY] [build.gradle.kts](file:///D:/Wininit/sc2fla/SCEventFishing/app/build.gradle.kts)
- Add `androidx.appcompat:appcompat` to support better locale management.

### 3. Theme Management
Update the theme implementation to support persistence and user selection.

#### [MODIFY] [Theme.kt](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/java/com/sceventhunters/sceventfishing/ui/theme/Theme.kt)
- No major changes needed to the Composable itself, but it will be called with persisted values.

### 4. Settings UI and Logic
Update `SettingsActivity.kt` to include UI for theme and language selection.

#### [MODIFY] [SettingsActivity.kt](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/java/com/sceventhunters/sceventfishing/SettingsActivity.kt)
- Add persistence functions for `ThemeMode` and `Language`.
- Add UI components for selecting Theme (System, Light, Dark).
- Add UI components for selecting Language (English, Russian).
- Implement logic to apply theme using state and language changes using `AppCompatDelegate`.

#### [MODIFY] [MainActivity.kt](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/java/com/sceventhunters/sceventfishing/MainActivity.kt)
- Observe the theme setting and apply it to `SCEventFishingTheme`.
- Ensure hardcoded strings like "Not Installed:" are replaced with `stringResource`.

## Verification Plan

### Automated Tests
- N/A (mostly UI and Resource changes).

### Manual Verification
1. **Language Switching**:
    - Open Settings.
    - Change language to Russian.
    - Verify that all UI elements in both `SettingsActivity` and `MainActivity` update to Russian.
    - Restart the app and verify the language is persisted.
2. **Theme Switching**:
    - Open Settings.
    - Change theme to Light/Dark/System.
    - Verify that the app theme updates immediately.
    - Restart the app and verify the theme is persisted.
3. **Localization**:
    - Ensure no hardcoded strings are missed (check all files for literal strings).
