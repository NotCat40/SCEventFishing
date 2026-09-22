# Implementation Plan - Restoring About to Settings and Improving Navigation

The goal is to move the "About" section back to the Settings screen, as it was redundant on the Main screen. The "About" button in the navigation drawer and top menu will now open the Settings screen and automatically scroll to the "About" section.

## User Review Required

> [!IMPORTANT]
> The "About" section is being removed from the `MainScreen`.
> The "About" button will now act as a shortcut to the bottom of the Settings screen.
> Scrolling functionality for "Brawl Stars" and "Clash Royale" in the drawer will be preserved as requested.

## Proposed Changes

### [Component Name] UI Layer

#### [MODIFY] [MainScreen.kt](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/java/com/sceventhunters/sceventfishing/ui/main/MainScreen.kt)
- Remove `AboutSection` and `AboutItem` composables.
- Remove `AboutSection` from the `LazyColumn` in both tablet and mobile layouts.
- Update `NavigationDrawerItem` for "About":
    - Instead of scrolling the `MainScreen`, it will launch `SettingsActivity` with an intent extra `scrollToAbout = true`.
- Update `DropdownMenuItem` for "About":
    - Similarly, launch `SettingsActivity` with the extra.

#### [MODIFY] [SettingsActivity.kt](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/java/com/sceventhunters/sceventfishing/SettingsActivity.kt)
- Extract the `scrollToAbout` flag from the intent.
- Pass this flag to the `SettingsScreen` composable.

#### [MODIFY] [SettingsScreen.kt](file:///D:/Wininit/sc2fla/SCEventFishing/app/src/main/java/com/sceventhunters/sceventfishing/ui/settings/SettingsScreen.kt)
- Add `scrollToAbout: Boolean = false` parameter.
- Use `LaunchedEffect` to detect when `scrollToAbout` is true and the scroll state is ready.
- Use `scrollState.animateScrollTo(scrollState.maxValue)` to scroll to the bottom where the "About" group is located.

## Verification Plan

### Automated Tests
- Verify successful compilation.

### Manual Verification
- **Drawer**: Click "Brawl Stars" / "Clash Royale" -> Verify scrolling within Main screen.
- **Drawer**: Click "About" -> Verify `SettingsActivity` opens and scrolls to the bottom.
- **Top Bar**: Click "About" in menu -> Verify `SettingsActivity` opens and scrolls to the bottom.
- **Main Screen**: Verify no "About" section is visible at the bottom of the app list.
