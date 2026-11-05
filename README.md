# AGUNG SETTINGS – OPTIMIZER

Android native starter project inspired by the supplied blue optimizer dashboard.

## Included
- Device identity: Android version, SDK, ABI, CPU cores, RAM.
- Reads maximum refresh rate reported by Android display modes.
- Shizuku permission flow.
- Requests system animation scales of 0.5x.
- Execution log and GitHub Actions APK build.

## Limitations
- Display refresh rate is not the same as in-game FPS.
- A normal app cannot universally force another game's FPS to 60.
- Touch sampling and 240 Hz drag response depend on hardware/vendor APIs.
- Fast-click automation and gameplay tap injection are not included.
- Game Driver selection is Android/vendor-dependent and is not modified here.
- No DPI or resolution changes.
- Shizuku must be running and authorized before system animation settings can be requested.

## Build APK on GitHub (no Android Studio)
1. Create a GitHub repository.
2. Upload this project's files to the repository root.
3. Open Actions and run **Build AGUNG SETTINGS APK**, or push to `main`.
4. Open the successful workflow run and download artifact **AGUNG-SETTINGS-APK**.
5. Extract the ZIP to get `app-debug.apk`.

## Local build
Requires JDK 17, Gradle 8.9+, and Android SDK Platform 35:
```bash
gradle assembleDebug
```
This is a debug APK, not a signed Play Store release.
