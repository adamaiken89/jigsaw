package com.jigsaw.app.ui.puzzle

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp

data class PieceData(
    val sourceRow: Int,
    val sourceCol: Int,
    val targetIndex: Int,
)

@Composable
fun PuzzleBoard(
    imageAsset: String,
    gridSize: Int,
    order: List<Int>,
    selectedIndex: Int?,
    completed: Boolean,
    onPieceTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(imageAsset) {
        try {
            val inputStream = context.assets.open(imageAsset)
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        } catch (_: Exception) {
            bitmap = null
        }
    }

    val pieces = remember(order, gridSize) {
        order.mapIndexed { slotIndex, pieceIndex ->
            val row = pieceIndex / gridSize
            val col = pieceIndex % gridSize
            PieceData(row, col, slotIndex)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        val boardSizeDp = maxWidth
        val pieceSizeDp = boardSizeDp / gridSize
        val gapDp = 1.dp
        val density = LocalDensity.current

        if (bitmap != null) {
            val bmp = bitmap!!

            pieces.forEachIndexed { slotIndex, piece ->
                val isSelected = slotIndex == selectedIndex && !completed
                val targetRow = slotIndex / gridSize
                val targetCol = slotIndex % gridSize

                val targetXDp = (targetCol * (pieceSizeDp + gapDp))
                val targetYDp = (targetRow * (pieceSizeDp + gapDp))

                val offsetX by animateDpAsState(
                    targetValue = targetXDp,
                    animationSpec = tween(durationMillis = if (completed) 600 else 300),
                    label = "offsetX$slotIndex"
                )
                val offsetY by animateDpAsState(
                    targetValue = targetYDp,
                    animationSpec = tween(durationMillis = if (completed) 600 else 300),
                    label = "offsetY$slotIndex"
                )

                val srcX = (piece.sourceCol * bmp.width) / gridSize
                val srcY = (piece.sourceRow * bmp.height) / gridSize
                val srcW = bmp.width / gridSize
                val srcH = bmp.height / gridSize

                val pieceBitmap = remember(piece.sourceRow, piece.sourceCol, bmp) {
                    Bitmap.createBitmap(bmp, srcX, srcY, srcW, srcH)
                }

                Box(
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .size(pieceSizeDp)
                        .graphicsLayer {
                            val scale = if (isSelected) 1.08f else 1f
                            scaleX = scale
                            scaleY = scale
                            shadowElevation = if (isSelected) 10f else 2f
                            shape = RoundedCornerShape(4.dp)
                            clip = true
                        }
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .then(
                            when {
                                isSelected -> Modifier.border(
                                    3.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp)
                                )
                                completed -> Modifier.border(
                                    0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp)
                                )
                                else -> Modifier.border(
                                    1.dp, Color.Black.copy(alpha = 0.12f), RoundedCornerShape(4.dp)
                                )
                            }
                        )
                        .pointerInput(completed) {
                            if (!completed) {
                                detectTapGestures { onTap() }
                            }
                        }
                ) {
                    androidx.compose.foundation.Image(
                        bitmap = pieceBitmap.asImageBitmap(),
                        contentDescription = "Puzzle piece",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (completed) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent)
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Loading puzzle...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
