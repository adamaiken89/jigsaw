# Android App Setup

## Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- Android SDK 35

## Building from terminal

```bash
cd android
./gradlew assembleDebug
```

`./gradlew` must exist — generate it via:
```bash
cd android && gradle wrapper --gradle-version 8.11.1
```
Or open the `android/` folder in Android Studio and let it sync.

## Adding game assets

### Puzzle images
Place high-resolution JPEG images in:
```
android/app/src/main/assets/images/
```
Then update the paths in `app/src/main/java/com/jigsaw/app/data/PuzzleRepository.kt`.

Supported formats: JPEG, PNG, WebP.

### Music tracks
Place MP3 files in:
```
android/app/src/main/res/raw/
```
File names must be lowercase, no spaces (e.g. `classical_piano.mp3`).
Then update `MusicTrack` entries in `app/src/main/java/com/jigsaw/app/service/MusicController.kt`.

Supported formats: MP3, OGG, WAV, FLAC.

## First build after adding assets
1. Place at least 3 puzzle images and 3 music files (see above)
2. Update `PuzzleRepository.kt` image paths and `MusicController.kt` track names
3. Run `./gradlew assembleDebug`
