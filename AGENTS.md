# Jigsaw — Agent Instructions

## Commands
- `npm run dev` — Start Vite dev server (default http://localhost:5173)
- `npm run build` — Typecheck + production build (`tsc -b && vite build`)
- `npm run lint` — ESLint on all project files
- No test runner configured. No typecheck-only script.

## Project structure
- **Single-page app** — no routing, no state library. Entry: `src/main.tsx`
- Puzzle logic and image slicing in `src/JigsawPuzzle.tsx`
- Puzzle config (images, grid sizes) and music track list in `src/config.ts`
- Game assets (Hubble images, classical MP3s) live under `public/assets/`
- UI styles in `src/App.css` and `src/index.css`

## TypeScript quirks
- `verbatimModuleSyntax: true` — use `import type` for type-only imports
- `noUnusedLocals` / `noUnusedParameters` — strict lint-level checks (build catches these too since `tsc -b` is part of `build`)
- `erasableSyntaxOnly: true` — no enums, no namespaces, no `constructor`-parameter properties

## Other
- ESLint flat config (`eslint.config.js`) — no `.eslintrc`
- `.github/copilot-instructions.md` is stale (old TODO list); ignore it
- No env files, no CI workflows, no docker, no infra

## Android app (`android/`)
- **Native Kotlin** app using Jetpack Compose + Material 3 + ViewModel + Navigation
- **Entry**: `android/app/src/main/java/com/jigsaw/app/MainActivity.kt`
- **Puzzle logic**: `PuzzleBoard.kt` (tap-two-pieces-to-swap), `PuzzleViewModel.kt`
- **Config + persistence**: `PuzzleRepository.kt`, `PreferencesManager.kt` (SharedPreferences)
- **Music**: `MusicController.kt` (MediaPlayer-based, not a Service)
- **Build**: `cd android && ./gradlew assembleDebug` (requires gradle wrapper, JDK 17+, SDK 35)
- **Game assets**: puzzle images go in `android/app/src/main/assets/images/`, music in `android/app/src/main/res/raw/`
- **No CI**, no test runner.</iati>

