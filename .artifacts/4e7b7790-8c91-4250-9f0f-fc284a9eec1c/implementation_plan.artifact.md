# Implementation Plan - App Modes (Regular, Compatibility, Demo)

This plan introduces three operating modes to the app:
1.  **Regular**: Current behavior (Root required, accesses `/data/data/...`).
2.  **Compatibility (SCHunt)**: Root not required. Files are read from a user-selected folder via Storage Access Framework (SAF).
3.  **Demo**: Root not required. Shows hardcoded demo data.

## Proposed Changes

### Data Models & Logic
- [NEW] Define `AppMode` enum: `REGULAR`, `COMPATIBILITY`, `DEMO`.
- [MODIFY] Refactor `MainActivity.kt` to abstract data fetching:
    - Create a `DataProvider` interface.
    - Implement `RootDataProvider`, `CompatibilityDataProvider`, and `DemoDataProvider`.
    - `CompatibilityDataProvider` will use `DocumentFile` to list files from a user-selected URI.

### Settings & Persistence
- [MODIFY] `SettingsActivity.kt`:
    - Add a "App Mode" selection (Dropdown or RadioButtons).
    - Add a "Select Folder" button, visible only in Compatibility mode.
    - Save the selected mode and the SAF folder URI in `SharedPreferences`.
- [MODIFY] `MainActivity.kt`:
    - Load the active mode on startup.
    - Update the UI to show the current mode in the TopAppBar or Drawer.
    - Handle folder selection result in `MainActivity` (or `SettingsActivity`) and take persistent URI permissions.

### UI Adjustments
- [MODIFY] `MainActivity.kt`:
    - Remove the hard Root block (`if (!rootAvailable) ...`). Instead, show a warning only if `REGULAR` mode is active and Root is missing.
    - In `Demo` mode, use mock icons and filenames.
    - In `Compatibility` mode, show a message if no folder is selected.

## Verification Plan

### Automated Tests
- N/A (UI and System integration focused).

### Manual Verification
1.  **Switch to Demo**: Go to Settings, select Demo mode. Verify the main screen shows demo apps and files without Root.
2.  **Switch to Compatibility**:
    *   Select Compatibility mode.
    *   Click "Select Folder" and pick a directory with some `.sc` or image files.
    *   Verify files from that directory appear in the app list.
3.  **Switch to Regular**:
    *   Select Regular mode.
    *   Verify the app requests Root and looks in the system cache (current behavior).
4.  **Persistence**: Change mode, restart app, verify mode is preserved.
5.  **Permissions**: Verify Compatibility mode works after a device restart (persistent URI permissions).
