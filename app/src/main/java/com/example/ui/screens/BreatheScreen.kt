package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DefaultBreathingPatterns
import com.example.ui.components.BreathingOrb
import com.example.ui.components.LiquidChip
import com.example.ui.components.LiquidPillButton
import com.example.ui.components.liquidGlass
import com.example.ui.theme.InterFont
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.viewmodel.MindfulnessViewModel

@Composable
fun BreatheScreen(
    viewModel: MindfulnessViewModel,
    modifier: Modifier = Modifier
) {
    val selectedPattern by viewModel.selectedBreathingPattern.collectAsState()
    val isRunning by viewModel.isBreathingActive.collectAsState()
    val currentPhase by viewModel.currentBreathPhase.collectAsState()
    val phaseProgress by viewModel.phaseProgress.collectAsState()
    val phaseRemaining by viewModel.phaseSecondsRemaining.collectAsState()
    val completedCycles by viewModel.completedBreathCycles.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen Title
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Liquid Breath",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Light,
                fontSize = 32.sp,
                color = OnSurface,
                letterSpacing = (-0.02).sp
            )
            Text(
                text = "Cadence for vagal equilibrium & nervous system calm",
                fontFamily = InterFont,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = OnSurfaceVariant,
                letterSpacing = 0.04.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pattern Selector Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(DefaultBreathingPatterns) { pattern ->
                LiquidChip(
                    text = pattern.name,
                    selected = selectedPattern.id == pattern.id,
                    onClick = { viewModel.selectBreathingPattern(pattern) },
                    testTag = "pattern_chip_${pattern.id}"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pattern detail spec glass card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(shape = RoundedCornerShape(20.dp), backgroundColor = Color(0x60121B2A))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedPattern.tag,
                        fontFamily = InterFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = Secondary,
                        letterSpacing = 0.08.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = selectedPattern.description,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = OnSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                // Rhythm badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color(0x207EE0D2))
                        .border(1.dp, Color(0x407EE0D2), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${selectedPattern.inhaleSec.toInt()}-${selectedPattern.holdInSec.toInt()}-${selectedPattern.exhaleSec.toInt()}-${selectedPattern.holdOutSec.toInt()}",
                        fontFamily = InterFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // The Liquid Breathing Orb
        BreathingOrb(
            currentPhase = currentPhase,
            phaseProgress = phaseProgress,
            phaseSecondsRemaining = phaseRemaining,
            pattern = selectedPattern,
            size = 280.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Guidance prompt text
        Text(
            text = currentPhase.subtitle,
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = if (isRunning) OnSurface else OnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Controls row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Haptic toggle button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (hapticEnabled) Color(0x28CCBEFF) else Color(0x12FFFFFF), CircleShape)
                    .border(
                        1.dp,
                        if (hapticEnabled) Color(0x60CCBEFF) else Color(0x20FFFFFF),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { viewModel.toggleHaptic() },
                    modifier = Modifier.size(46.dp).testTag("haptic_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = "Toggle Tactile Feedback",
                        tint = if (hapticEnabled) Secondary else OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Main Primary Luminous Pill Button
            LiquidPillButton(
                text = if (isRunning) "Pause Stillness" else "Begin Cadence",
                onClick = { viewModel.toggleBreathing() },
                isPrimary = true,
                testTag = "breathing_action_button",
                leadingIcon = {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp).padding(end = 4.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.width(18.dp))

            // Cycles counter pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(Color(0x18FFFFFF), RoundedCornerShape(9999.dp))
                    .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(9999.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$completedCycles cycles",
                    fontFamily = InterFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = OnSurfaceVariant,
                    letterSpacing = 0.04.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
