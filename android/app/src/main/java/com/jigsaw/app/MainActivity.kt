package com.jigsaw.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigsaw.app.ui.components.MusicBar
import com.jigsaw.app.ui.home.HomeScreen
import com.jigsaw.app.ui.home.HomeViewModel
import com.jigsaw.app.ui.puzzle.PuzzleScreen
import com.jigsaw.app.ui.puzzle.PuzzleViewModel
import com.jigsaw.app.ui.theme.JigsawTheme
import com.jigsaw.app.data.PuzzleRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as JigsawApplication
        val prefs = app.prefs
        val musicController = app.musicController

        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val puzzleViewModel = ViewModelProvider(this)[PuzzleViewModel::class.java]

        setContent {
            JigsawTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

                val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
                val puzzleState by puzzleViewModel.uiState.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = currentScreen is Screen.Puzzle && puzzleState.isStarted,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it }),
                        ) {
                            MusicBar(
                                volume = prefs.getMusicVolume(),
                                isMuted = prefs.isMusicMuted(),
                                isPlaying = musicController.isPlaying,
                                onVolumeChange = { vol ->
                                    prefs.setMusicVolume(vol)
                                    prefs.setMusicMuted(vol == 0f)
                                    musicController.setVolume(vol, vol == 0f)
                                    if (!musicController.isPlaying) {
                                        musicController.play(prefs)
                                    }
                                },
                                onMuteToggle = {
                                    val muted = !prefs.isMusicMuted()
                                    prefs.setMusicMuted(muted)
                                    musicController.setVolume(prefs.getMusicVolume(), muted)
                                    if (muted.not() && !musicController.isPlaying) {
                                        musicController.play(prefs)
                                    }
                                },
                            )
                        }
                    }
                ) { innerPadding ->
                    when (val screen = currentScreen) {
                        is Screen.Home -> {
                            LaunchedEffect(Unit) {
                                homeViewModel.refresh()
                            }
                            HomeScreen(
                                uiState = homeState,
                                onPuzzleClick = { puzzleId ->
                                    puzzleViewModel.loadPuzzle(puzzleId)
                                    currentScreen = Screen.Puzzle
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        is Screen.Puzzle -> {
                            PuzzleScreen(
                                uiState = puzzleState,
                                onPieceTap = { puzzleViewModel.onPieceTap(it) },
                                onStart = { puzzleViewModel.startPuzzle() },
                                onBack = {
                                    currentScreen = Screen.Home
                                    homeViewModel.refresh()
                                },
                                onDismissConfetti = { puzzleViewModel.dismissConfetti() },
                                onNextPuzzle = {
                                    val nextId = puzzleState.puzzleId + 1
                                    if (nextId < PuzzleRepository.count) {
                                        puzzleViewModel.loadPuzzle(nextId)
                                    }
                                },
                                onPreviousPuzzle = {
                                    val prevId = puzzleState.puzzleId - 1
                                    if (prevId >= 0) {
                                        puzzleViewModel.loadPuzzle(prevId)
                                    }
                                },
                                modifier = Modifier.padding(innerPadding)
                            )

                            LaunchedEffect(puzzleState.isStarted) {
                                if (puzzleState.isStarted) {
                                    musicController.play(prefs)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen {
    data object Home : Screen()
    data object Puzzle : Screen()
}
