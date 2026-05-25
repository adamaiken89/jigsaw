package com.jigsaw.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    val speed: Float,
    val color: Color,
    val rotation: Float,
    val rotationSpeed: Float,
    val size: Float,
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 80,
    onFinished: () -> Unit = {}
) {
    val colors = listOf(
        Color(0xFFFF6B6B), Color(0xFFFFD93D), Color(0xFF6BCB77),
        Color(0xFF4D96FF), Color(0xFFFF8E53), Color(0xFFA66CFF),
        Color(0xFFFF6B9D), Color(0xFF00D2FF),
    )

    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = -Random.nextFloat() * 0.5f,
                speed = 0.3f + Random.nextFloat() * 0.7f,
                color = colors.random(),
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 10f,
                size = 6f + Random.nextFloat() * 8f,
            )
        }
    }

    var finished by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "progress",
    )

    if (!finished) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            var allDone = true

            particles.forEach { p ->
                val pct = ((progress + p.x) % 1f).toDouble()
                val yPos = (pct * (height * 1.5f) - height * 0.25f).toFloat()
                if (yPos < height + 50f) allDone = false

                val alpha = when {
                    yPos < 0f -> (yPos + height * 0.25f) / (height * 0.25f)
                    yPos > height * 0.7f -> 1f - (yPos - height * 0.7f) / (height * 0.3f)
                    else -> 1f
                }.coerceIn(0f, 1f)

                val sway = kotlin.math.sin(pct * 8.0 + p.x * 10.0).toFloat() * 60f

                rotate(
                    degrees = p.rotation + pct.toFloat() * p.rotationSpeed * 100f,
                    pivot = Offset(p.x * width + sway, yPos)
                ) {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(p.x * width + sway - p.size / 2, yPos - p.size / 4),
                        size = androidx.compose.ui.geometry.Size(p.size, p.size * 0.6f),
                    )
                }
            }

            if (allDone) {
                finished = true
                onFinished()
            }
        }
    }
}
