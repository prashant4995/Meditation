package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LiquidPillButton
import com.example.ui.components.liquidGlass
import com.example.ui.theme.GlassTealBloom
import com.example.ui.theme.InterFont
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.Tertiary
import com.example.viewmodel.MindfulnessViewModel

@Composable
fun TimerScreen(
    viewModel: MindfulnessViewModel,
    modifier: Modifier = Modifier
) {
    val targetMinutes by viewModel.timerTargetMinutes.collectAsState()
    val remainingSeconds by viewModel.timerRemainingSeconds.collectAsState()
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val intervalBells by viewModel.intervalBellMinutes.collectAsState()
    val intention by viewModel.timerIntention.collectAsState()

    val totalSeconds = targetMinutes * 60
    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 0f

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Zen Timer",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Light,
                fontSize = 32.sp,
                color = OnSurface,
                letterSpacing = (-0.02).sp
            )
            Text(
                text = "Unstructured mindful presence anchored by sacred bells",
                fontFamily = InterFont,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = OnSurfaceVariant,
                letterSpacing = 0.04.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Circular Liquid Glass Timer Dial
        Box(
            modifier = Modifier
                .size(260.dp)
                .testTag("zen_timer_dial"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(260.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val strokeWidth = 5.dp.toPx()
                val radius = (size.minDimension / 2f) - strokeWidth - 10.dp.toPx()

                // Ambient halo background
                drawCircle(
                    brush = Brush.radialGradient(
                        0.0f to Color(0x357EE0D2),
                        0.6f to Color(0x15CCBEFF),
                        1.0f to Color.Transparent,
                        center = center,
                        radius = radius * 1.25f
                    ),
                    radius = radius * 1.25f,
                    center = center
                )

                // Track ring
                drawCircle(
                    color = Color(0x18FFFFFF),
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // Active progress arc
                val sweep = 360f * progress
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to Secondary,
                        0.5f to Primary,
                        1.0f to Secondary,
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Top specular reflection on ring
                drawCircle(
                    brush = Brush.sweepGradient(
                        0.0f to Color(0x40FFFFFF),
                        0.5f to Color.Transparent,
                        1.0f to Color(0x40FFFFFF),
                        center = center
                    ),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formattedTime,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Light,
                    fontSize = 48.sp,
                    color = OnSurface,
                    letterSpacing = (-0.02).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isRunning) "Remaining" else "Target: $targetMinutes min",
                    fontFamily = InterFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Primary,
                    letterSpacing = 0.05.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Intention Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(shape = RoundedCornerShape(18.dp), backgroundColor = Color(0x55111928))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "“$intention”",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = Secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Duration Slider (only editable when not running)
        if (!isRunning) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(shape = RoundedCornerShape(20.dp), backgroundColor = Color(0x50131C2D))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Session Duration",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = OnSurface
                    )
                    Text(
                        text = "$targetMinutes min",
                        fontFamily = InterFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Primary
                    )
                }

                Slider(
                    value = targetMinutes.toFloat(),
                    onValueChange = { viewModel.setTimerTargetMinutes(it.toInt()) },
                    valueRange = 1f..60f,
                    steps = 58,
                    modifier = Modifier.fillMaxWidth().testTag("timer_duration_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = Primary,
                        activeTrackColor = Primary,
                        inactiveTrackColor = Color(0x25FFFFFF)
                    )
                )

                // Interval bell pills
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Interval Bells:",
                            fontFamily = InterFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(0 to "Off", 2 to "2m", 5 to "5m").forEach { (min, label) ->
                            val isSelected = intervalBells == min
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(if (isSelected) Color(0x357EE0D2) else Color(0x15FFFFFF))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0x607EE0D2) else Color(0x20FFFFFF),
                                        RoundedCornerShape(9999.dp)
                                    )
                                    .clickable { viewModel.setIntervalBellMinutes(min) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontFamily = InterFont,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Primary else OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRunning || remainingSeconds < totalSeconds) {
                // Reset button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0x18FFFFFF), CircleShape)
                        .border(1.dp, Color(0x25FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { viewModel.resetTimer() },
                        modifier = Modifier.size(46.dp).testTag("timer_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Timer",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
            }

            LiquidPillButton(
                text = if (isRunning) "Pause Silence" else "Commence Stillness",
                onClick = { viewModel.toggleTimer() },
                isPrimary = true,
                testTag = "timer_action_button",
                leadingIcon = {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp).padding(end = 4.dp)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
