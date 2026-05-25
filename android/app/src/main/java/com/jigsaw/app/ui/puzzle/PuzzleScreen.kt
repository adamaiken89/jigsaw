package com.jigsaw.app.ui.puzzle

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigsaw.app.ui.components.ConfettiEffect
import com.jigsaw.app.data.PuzzleRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleScreen(
    uiState: PuzzleUiState,
    onPieceTap: (Int) -> Unit,
    onStart: () -> Unit,
    onBack: () -> Unit,
    onDismissConfetti: () -> Unit,
    onNextPuzzle: () -> Unit,
    onPreviousPuzzle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = uiState.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            if (uiState.isStarted) {
                                Text(
                                    text = "${uiState.gridSize} × ${uiState.gridSize} · Puzzle ${uiState.puzzleId + 1} of ${PuzzleRepository.count}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                    actions = {
                        if (uiState.isStarted) {
                            IconButton(
                                onClick = onPreviousPuzzle,
                                enabled = uiState.puzzleId > 0
                            ) {
                                Icon(Icons.Default.ArrowBack, "Previous")
                            }
                            IconButton(
                                onClick = onNextPuzzle,
                                enabled = uiState.completed && uiState.puzzleId < PuzzleRepository.count - 1
                            ) {
                                Icon(Icons.Default.ArrowForward, "Next")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    )
                )
            },
            modifier = modifier
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (!uiState.isStarted && !uiState.completed) {
                    StartPrompt(
                        gridSize = uiState.gridSize,
                        onStart = onStart,
                        puzzleId = uiState.puzzleId,
                    )
                } else {
                    PuzzleBoard(
                        imageAsset = uiState.imageAsset,
                        gridSize = uiState.gridSize,
                        order = uiState.order,
                        selectedIndex = uiState.selectedIndex,
                        completed = uiState.completed,
                        onPieceTap = onPieceTap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.completed) {
                        CompletionMessage()
                    } else if (uiState.selectedIndex != null) {
                        Text(
                            text = "Tap another piece to swap",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    } else {
                        Text(
                            text = "Tap two pieces to swap them",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        )
                    }
                }
            }
        }

        if (uiState.showConfetti) {
            ConfettiEffect(
                onFinished = onDismissConfetti,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun StartPrompt(
    gridSize: Int,
    onStart: () -> Unit,
    puzzleId: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ConfirmationNumber,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = "Puzzle ${puzzleId + 1}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "${gridSize} × ${gridSize} Grid",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = "Tap each piece to select it,\nthen tap another to swap.\nArrange all pieces to complete the image!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 32.dp),
        )

        Button(
            onClick = onStart,
            modifier = Modifier
                .padding(top = 8.dp)
                .height(56.dp)
                .widthIn(min = 200.dp),
            shape = RoundedCornerShape(28.dp),
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Start Puzzle",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun CompletionMessage() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🎉",
                fontSize = 48.sp,
            )
            Text(
                text = "Puzzle Completed!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Great job! All pieces are in place.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
    }
}
