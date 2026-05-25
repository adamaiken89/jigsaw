package com.jigsaw.app.ui.puzzle

import androidx.lifecycle.ViewModel
import com.jigsaw.app.data.PreferencesManager
import com.jigsaw.app.data.PuzzleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PuzzleUiState(
    val puzzleId: Int = 0,
    val imageAsset: String = "",
    val gridSize: Int = 4,
    val title: String = "",
    val order: List<Int> = emptyList(),
    val selectedIndex: Int? = null,
    val completed: Boolean = false,
    val showConfetti: Boolean = false,
    val isStarted: Boolean = false,
)

class PuzzleViewModel(private val prefs: PreferencesManager) : ViewModel() {

    private val _uiState = MutableStateFlow(PuzzleUiState())
    val uiState: StateFlow<PuzzleUiState> = _uiState.asStateFlow()

    fun loadPuzzle(puzzleId: Int) {
        val puzzle = PuzzleRepository.getById(puzzleId)
        val indices = (0 until puzzle.gridSize * puzzle.gridSize).toList()
        _uiState.value = PuzzleUiState(
            puzzleId = puzzle.id,
            imageAsset = puzzle.imageAsset,
            gridSize = puzzle.gridSize,
            title = puzzle.title,
            order = if (prefs.isPuzzleCompleted(puzzle.id)) indices else indices.shuffled(),
            completed = prefs.isPuzzleCompleted(puzzle.id),
            isStarted = prefs.isPuzzleCompleted(puzzle.id),
            showConfetti = false,
        )
    }

    fun startPuzzle() {
        val state = _uiState.value
        if (!state.isStarted && !state.completed) {
            val indices = (0 until state.gridSize * state.gridSize).toList()
            _uiState.value = state.copy(
                isStarted = true,
                order = indices.shuffled(),
                selectedIndex = null,
            )
        }
    }

    fun onPieceTap(slotIndex: Int) {
        val state = _uiState.value
        if (!state.isStarted || state.completed) return

        val currentSelected = state.selectedIndex
        val newOrder = state.order.toMutableList()

        if (currentSelected == null) {
            _uiState.value = state.copy(selectedIndex = slotIndex)
        } else if (currentSelected == slotIndex) {
            _uiState.value = state.copy(selectedIndex = null)
        } else {
            val temp = newOrder[currentSelected]
            newOrder[currentSelected] = newOrder[slotIndex]
            newOrder[slotIndex] = temp

            val solved = newOrder.indices.all { newOrder[it] == it }
            if (solved) {
                prefs.setPuzzleCompleted(state.puzzleId)
                prefs.unlockNextPuzzle(state.puzzleId)
                _uiState.value = state.copy(
                    order = newOrder,
                    selectedIndex = null,
                    completed = true,
                    showConfetti = true,
                )
            } else {
                _uiState.value = state.copy(
                    order = newOrder,
                    selectedIndex = null,
                )
            }
        }
    }

    fun dismissConfetti() {
        _uiState.value = _uiState.value.copy(showConfetti = false)
    }
}
