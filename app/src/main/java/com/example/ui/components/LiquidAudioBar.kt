package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SoundscapeItem
import com.example.ui.theme.GlassTealBloom
import com.example.ui.theme.InterFont
import com.example.ui.theme.OnSurface
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary

@Composable
fun LiquidAudioBar(
    soundscape: SoundscapeItem?,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onOpenSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val barShape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .testTag("liquid_audio_bar")
            .fillMaxWidth()
            .clip(barShape)
            .background(Color(0xCC111B2C), barShape)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color(0x40FFFFFF),
                        Color(0x207EE0D2),
                        Color(0x15CCBEFF)
                    )
                ),
                barShape
            )
            .drawBehind {
                // Top glare reflection line
                drawLine(
                    brush = Brush.horizontalGradient(
                        0.0f to Color.Transparent,
                        0.25f to Color(0x35FFFFFF),
                        0.75f to Color(0x35FFFFFF),
                        1.0f to Color.Transparent
                    ),
                    start = Offset(12.dp.toPx(), 1.dp.toPx()),
                    end = Offset(size.width - 12.dp.toPx(), 1.dp.toPx()),
                    strokeWidth = 1.2f
                )
            }
            .clickable(onClick = onOpenSheet)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Animated Soundwave visualizer
            SoundwaveVisualizer(
                isPlaying = isPlaying,
                tintColor = soundscape?.tintColor ?: Primary,
                modifier = Modifier
                    .width(32.dp)
                    .height(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Track info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = soundscape?.title ?: "Ambient Soundscape",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = OnSurface,
                    maxLines = 1
                )
                Text(
                    text = soundscape?.frequencyLabel ?: "Select tranquil soundscape",
                    fontFamily = InterFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = Secondary,
                    letterSpacing = 0.04.sp,
                    maxLines = 1
                )
            }

            // Play / Pause pill button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0x307EE0D2), CircleShape)
                    .border(1.dp, Color(0x607EE0D2), CircleShape)
                    .drawBehind {
                        if (isPlaying) {
                            drawCircle(GlassTealBloom.copy(alpha = 0.35f))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("audio_bar_play_toggle")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause Soundscape" else "Play Soundscape",
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SoundwaveVisualizer(
    isPlaying: Boolean,
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "soundwave")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(620, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(520, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w3"
    )
    val wave4 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(710, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w4"
    )

    Canvas(modifier = modifier) {
        val barCount = 4
        val totalWidth = size.width
        val barWidth = 3.dp.toPx()
        val spacing = (totalWidth - (barCount * barWidth)) / (barCount - 1)

        val heights = if (isPlaying) {
            listOf(wave1, wave2, wave3, wave4)
        } else {
            listOf(0.3f, 0.45f, 0.3f, 0.2f)
        }

        for (i in 0 until barCount) {
            val h = size.height * heights[i]
            val x = i * (barWidth + spacing)
            val y = (size.height - h) / 2f

            drawRoundRect(
                color = tintColor,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }
}
