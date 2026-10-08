package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BreathPhase
import com.example.data.BreathingPattern
import com.example.ui.theme.InterFont
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import kotlin.math.sin

@Composable
fun BreathingOrb(
    currentPhase: BreathPhase,
    phaseProgress: Float, // 0f to 1f
    phaseSecondsRemaining: Float,
    pattern: BreathingPattern,
    modifier: Modifier = Modifier,
    size: Dp = 290.dp
) {
    // Continuous subtle ethereal rotation for glass refraction
    val infiniteTransition = rememberInfiniteTransition(label = "glass_shimmer")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_rotation"
    )

    val pulseScale = remember { Animatable(0.65f) }

    // Target scale based on breath phase
    val targetScale = when (currentPhase) {
        BreathPhase.INHALE -> 0.65f + 0.35f * phaseProgress
        BreathPhase.HOLD_IN -> 1.0f
        BreathPhase.EXHALE -> 1.0f - 0.35f * phaseProgress
        BreathPhase.HOLD_OUT -> 0.65f
    }

    LaunchedEffect(targetScale) {
        pulseScale.animateTo(
            targetValue = targetScale,
            animationSpec = tween(120, easing = LinearEasing)
        )
    }

    Box(
        modifier = modifier
            .testTag("breathing_guide_orb")
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val maxRadius = (this.size.minDimension / 2f) - 16.dp.toPx()
            val currentRadius = maxRadius * pulseScale.value

            // 1. Ambient outer aura diffusion
            drawCircle(
                brush = Brush.radialGradient(
                    0.0f to pattern.accentColor.copy(alpha = 0.28f * pulseScale.value),
                    0.7f to Secondary.copy(alpha = 0.12f * pulseScale.value),
                    1.0f to Color.Transparent,
                    center = center,
                    radius = maxRadius * 1.35f
                ),
                radius = maxRadius * 1.35f,
                center = center
            )

            // 2. Expanding outer ripple ring
            val rippleRadius = currentRadius + 22.dp.toPx() * (1f + 0.2f * sin(phaseProgress * Math.PI.toFloat()))
            drawCircle(
                brush = Brush.sweepGradient(
                    0.0f to pattern.accentColor.copy(alpha = 0.35f),
                    0.5f to Secondary.copy(alpha = 0.15f),
                    1.0f to pattern.accentColor.copy(alpha = 0.35f),
                    center = center
                ),
                radius = rippleRadius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // 3. Middle translucent glass aura
            drawCircle(
                brush = Brush.radialGradient(
                    0.0f to pattern.accentColor.copy(alpha = 0.45f),
                    0.55f to Color(0x351F334A),
                    0.9f to Color(0x200F1725),
                    1.0f to Color.Transparent,
                    center = center,
                    radius = currentRadius * 1.15f
                ),
                radius = currentRadius * 1.15f,
                center = center
            )

            // 4. Core liquid glass orb body
            rotate(rotationAngle, pivot = center) {
                drawCircle(
                    brush = Brush.linearGradient(
                        0.0f to pattern.accentColor.copy(alpha = 0.85f),
                        0.35f to Color(0xCC7EE0D2),
                        0.75f to Color(0x99C4B5FD),
                        1.0f to Color(0x70322258),
                        start = Offset(center.x - currentRadius, center.y - currentRadius),
                        end = Offset(center.x + currentRadius, center.y + currentRadius)
                    ),
                    radius = currentRadius,
                    center = center
                )
            }

            // 5. Specular rim reflection (delicate 1.5px glass border)
            drawCircle(
                brush = Brush.sweepGradient(
                    0.0f to Color(0xE6FFFFFF),
                    0.25f to Color(0x33FFFFFF),
                    0.6f to pattern.accentColor.copy(alpha = 0.9f),
                    0.85f to Color(0x22FFFFFF),
                    1.0f to Color(0xE6FFFFFF),
                    center = center
                ),
                radius = currentRadius,
                center = center,
                style = Stroke(width = 1.8.dp.toPx())
            )

            // 6. Top inner glass glare highlight
            drawCircle(
                brush = Brush.radialGradient(
                    0.0f to Color(0x99FFFFFF),
                    0.4f to Color(0x30FFFFFF),
                    1.0f to Color.Transparent,
                    center = Offset(center.x - currentRadius * 0.28f, center.y - currentRadius * 0.35f),
                    radius = currentRadius * 0.5f
                ),
                radius = currentRadius * 0.5f,
                center = Offset(center.x - currentRadius * 0.28f, center.y - currentRadius * 0.35f)
            )
        }

        // Center typography indicators
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentPhase.label,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                color = OnSurface,
                letterSpacing = (-0.01).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = String.format("%.1fs", phaseSecondsRemaining),
                fontFamily = InterFont,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Primary,
                letterSpacing = 0.05.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = pattern.name,
                fontFamily = InterFont,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = OnSurfaceVariant,
                letterSpacing = 0.08.sp
            )
        }
    }
}
