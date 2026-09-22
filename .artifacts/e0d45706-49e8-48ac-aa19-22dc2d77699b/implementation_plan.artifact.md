# Implementation Plan - Extract Hardcoded Strings to strings.xml

Move all hardcoded user-facing strings into `strings.xml` across all layout XML files and Compose screens (`MainActivity.kt` and `SettingsActivity.kt`) to ensure localized, maintainable, and clean code.

## User Review Required

> [!NOTE]
> All user-facing strings will be extracted. Some developer-facing internal strings such as package names, shared preference keys, file extensions (`.sc`, `.jpg`), or command-line segments will remain in place as code literals.

## Proposed Changes

### 1. Resources Layer

#### [MODIFY] [strings.xml](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/res/values/strings.xml)
- Define new string resources covering all hardcoded titles, labels, buttons, descriptions, and string templates with placeholders.

### 2. XML Layouts

#### [MODIFY] [activity_main.xml](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/res/layout/activity_main.xml)
- Replace `android:text="Enable Overlay"` with `@string/enable_overlay`.
- Replace `android:text="No monitored apps installed."` with `@string/no_monitored_apps`.

#### [MODIFY] [list_item_app.xml](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/res/layout/list_item_app.xml)
- Replace `android:text="Launch"` with `@string/launch`.

### 3. Jetpack Compose Screens

#### [MODIFY] [MainActivity.kt](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/java/com/sceventhunters/sceventfishing/MainActivity.kt)
- Use `context.getString(...)` or `stringResource(...)` for all hardcoded text components, toasts, and accessibility description elements.

#### [MODIFY] [SettingsActivity.kt](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/java/com/sceventhunters/sceventfishing/SettingsActivity.kt)
- Use `stringResource(...)` to replace hardcoded strings within labels, list items, description strings, and the About section items.

---

## Verification Plan

### Automated Tests
- Run Gradle assemble to verify code compilation with new resource resolution:
  `gradle_build("app:assembleDebug")`

### Manual Verification
- Verify code readability and check that layout files correctly link to the strings repository.
