package com.jigsaw.app.data

data class PuzzleConfig(
    val id: Int,
    val imageAsset: String,
    val gridSize: Int,
    val title: String
)

data class PuzzleProgress(
    val completed: Boolean = false
)

object PuzzleRepository {
    val puzzles = listOf(
        PuzzleConfig(0, "images/puzzle1.jpg", 4, "Nebula"),
        PuzzleConfig(1, "images/puzzle2.jpg", 5, "Galaxy"),
        PuzzleConfig(2, "images/puzzle3.jpg", 6, "Star Cluster"),
    )

    fun getById(id: Int): PuzzleConfig = puzzles.first { it.id == id }
    val count: Int get() = puzzles.size
}
