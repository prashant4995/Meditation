package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GuidedMeditation
import com.example.ui.components.LiquidPillButton
import com.example.ui.components.SoundwaveVisualizer
import com.example.ui.components.liquidElevatedGlass
import com.example.ui.components.liquidGlass
import com.example.ui.theme.Background
import com.example.ui.theme.InterFont
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.Tertiary
import com.example.viewmodel.MindfulnessViewModel

@Composable
fun SessionActiveScreen(
    meditation: GuidedMeditation,
    viewModel: MindfulnessViewModel,
    onFinish: () -> Unit
) {
    val elapsedSeconds by viewModel.meditationElapsedSeconds.collectAsState()
    val promptIndex by viewModel.meditationPromptIndex.collectAsState()
    val isSoundscapePlaying by viewModel.isSoundscapePlaying.collectAsState()

    val totalSeconds = meditation.durationMinutes * 60
    val progress = (elapsedSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

    val currentPrompt = if (promptIndex < meditation.guidePrompts.size) {
        meditation.guidePrompts[promptIndex]
    } else {
        "Rest in the peaceful stillness of completion."
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .testTag("active_meditation_screen")
    ) {
        // Ambient background aura
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.45f)
            drawCircle(
                brush = Brush.radialGradient(
                    0.0f to meditation.accentColor.copy(alpha = 0.22f),
                    0.5f to Secondary.copy(alpha = 0.08f),
                    1.0f to Color.Transparent,
                    center = center,
                    radius = size.width * 0.65f
                ),
                radius = size.width * 0.65f,
                center = center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.exitGuidedMeditation()
                        onFinish()
                    },
                    modifier = Modifier.testTag("exit_meditation_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Journey",
                        tint = OnSurfaceVariant
                    )
                }

                Text(
                    text = meditation.title,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = OnSurface
                )

                // Soundwave visualizer
                SoundwaveVisualizer(
                    isPlaying = isSoundscapePlaying,
                    tintColor = meditation.accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Center: Liquid Glass Halo & Animated Mindful Prompts
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                // Circular ring
                Box(
                    modifier = Modifier.size(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(220.dp)) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val r = (size.minDimension / 2f) - 12.dp.toPx()

                        drawCircle(
                            color = Color(0x18FFFFFF),
                            radius = r,
                            center = center,
                            style = Stroke(width = 3.dp.toPx())
                        )

                        drawArc(
                            brush = Brush.sweepGradient(
                                0.0f to meditation.accentColor,
                                0.7f to Secondary,
                                1.0f to meditation.accentColor,
                                center = center
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timeFormatted,
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Light,
                            fontSize = 36.sp,
                            color = OnSurface
                        )
                        Text(
                            text = "of ${meditation.durationMinutes}:00",
                            fontFamily = InterFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Mindful Prompt card with smooth cross-fade animation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidElevatedGlass(
                            shape = RoundedCornerShape(24.dp),
                            backgroundColor = Color(0x80131B2A)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = currentPrompt,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "prompt_animation"
                    ) { prompt ->
                        Text(
                            text = "“$prompt”",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Light,
                            fontSize = 18.sp,
                            color = OnSurface,
                            textAlign = TextAlign.Center,
                            lineHeight = 26.sp
                        )
                    }
                }
            }

            // Bottom Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(9999.dp)),
                    color = meditation.accentColor,
                    trackColor = Color(0x20FFFFFF),
                )

                Spacer(modifier = Modifier.height(20.dp))

                LiquidPillButton(
                    text = if (progress >= 1f) "Conclude Journey" else "Complete in Peace",
                    onClick = {
                        viewModel.exitGuidedMeditation()
                        onFinish()
                    },
                    isPrimary = true,
                    testTag = "conclude_meditation_btn"
                )
            }
        }
    }
}
