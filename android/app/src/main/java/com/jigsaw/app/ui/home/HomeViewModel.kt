package com.jigsaw.app.ui.home

import androidx.lifecycle.ViewModel
import com.jigsaw.app.data.PreferencesManager
import com.jigsaw.app.data.PuzzleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val unlockedCount: Int = 1,
    val puzzles: List<PuzzleCardState> = emptyList()
)

data class PuzzleCardState(
    val id: Int,
    val title: String,
    val gridSize: Int,
    val isUnlocked: Boolean,
    val isCompleted: Boolean
)

class HomeViewModel(private val prefs: PreferencesManager) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val unlocked = prefs.getLastUnlockedPuzzle().coerceIn(0, PuzzleRepository.count - 1)
        _uiState.value = HomeUiState(
            unlockedCount = unlocked + 1,
            puzzles = PuzzleRepository.puzzles.map { puzzle ->
                PuzzleCardState(
                    id = puzzle.id,
                    title = puzzle.title,
                    gridSize = puzzle.gridSize,
                    isUnlocked = puzzle.id <= unlocked,
                    isCompleted = prefs.isPuzzleCompleted(puzzle.id)
                )
            }
        )
    }
}
