# Android 16 AccessibilityService Bug Reproducer

Minimal reproducer app for [Google Issue #470114250](https://issuetracker.google.com/issues/470114250).

## Download

**[Download APK](https://github.com/d4rken/google-issue-470114250/releases/latest/download/app-debug.apk)**

Or install via adb:
```
adb install app-debug.apk
```

## The Bug

**AccessibilityService tree excludes clickable nodes visible to UIAutomator in Settings**

On Android 16, certain UI elements in the Settings app are completely invisible to AccessibilityService, even though they are visible on screen and accessible via UIAutomator. Specifically, the "Clear storage" and "Clear cache" buttons on the Storage & cache screen are missing from the accessibility node hierarchy.

### Affected Versions

- Android 16 Beta (CP11.251114.006, ZP11.251121.011)
- Works correctly on Android 15 and earlier

### Impact

This bug breaks:
- Accessibility apps
- Enterprise device management tools
- Automation frameworks
- App management utilities relying on legitimate AccessibilityService access

## Usage

1. Install the app on an **Android 16** device
2. Open the app and tap **"Open Accessibility Settings"**
3. Find and enable **"Debug Accessibility Service"**
4. Return to the app and tap **"Open GMS App Info"**
5. Tap **"Storage & cache"** in the Google Play Services settings
6. Check logcat for the accessibility tree dump:
   ```
   adb logcat -s DebugA11y
   ```

## Expected vs Actual

### Expected (Android 15)

The accessibility tree should include the action buttons:
```
[LinearLayout] id=action1
  [Button] text="Clear storage" CLICKABLE
[LinearLayout] id=action2
  [Button] text="Clear cache" CLICKABLE
```

### Actual (Android 16)

There is a spatial gap in the hierarchy where buttons should appear:
```
[entity_header_content] bounds=Rect(..., 783)  <- Header ends at y=783
  [TextView] "Google Play services"

                                                <- GAP: buttons missing!

[LinearLayout] bounds=Rect(..., 1137, ...)     <- "Space used" starts at y=1137
  [TextView] "Space used"
```

The buttons exist in the view hierarchy (UIAutomator can see them) but are completely absent from the AccessibilityService tree.

## Technical Details

The service is configured with:
- `flagIncludeNotImportantViews` - to include views not important for accessibility
- `flagReportViewIds` - to report view resource IDs
- `canRetrieveWindowContent="true"` - to access window content

Despite these flags, the buttons remain invisible to the accessibility service.

## Links

- [Issue Tracker](https://issuetracker.google.com/issues/470114250)
- [This Repository](https://github.com/d4rken/google-issue-470114250)

## License

This is a bug reproducer sample. Use freely.
