package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DefaultGuidedMeditations
import com.example.data.DefaultSoundscapes
import com.example.data.GuidedMeditation
import com.example.data.SoundscapeItem
import com.example.ui.components.LiquidChip
import com.example.ui.components.liquidGlass
import com.example.ui.theme.InterFont
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.Tertiary
import com.example.viewmodel.MindfulnessViewModel

@Composable
fun ExploreScreen(
    viewModel: MindfulnessViewModel,
    onStartMeditation: (GuidedMeditation) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeSoundscape by viewModel.currentSoundscape.collectAsState()
    val isPlaying by viewModel.isSoundscapePlaying.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    val filterTabs = listOf("All", "Soundscapes", "Guided Journeys", "Sleep", "Stress")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sanctuary",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Light,
                fontSize = 32.sp,
                color = OnSurface,
                letterSpacing = (-0.02).sp
            )
            Text(
                text = "Atmospheric acoustic frequencies & guided journeys",
                fontFamily = InterFont,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = OnSurfaceVariant,
                letterSpacing = 0.04.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filters
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(filterTabs) { tab ->
                    LiquidChip(
                        text = tab,
                        selected = selectedFilter == tab,
                        onClick = { selectedFilter = tab },
                        testTag = "filter_tab_$tab"
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section: Soundscapes
        if (selectedFilter in listOf("All", "Soundscapes", "Sleep")) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Procedural Soundscapes",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = OnSurface
                    )
                    Text(
                        text = "Infinite Loops",
                        fontFamily = InterFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Secondary,
                        letterSpacing = 0.05.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(DefaultSoundscapes) { soundscape ->
                val isCurrent = activeSoundscape?.id == soundscape.id
                val isCurrentlyPlaying = isCurrent && isPlaying

                SoundscapeCard(
                    soundscape = soundscape,
                    isPlaying = isCurrentlyPlaying,
                    onTap = {
                        if (isCurrent) {
                            viewModel.toggleSoundscapePlay()
                        } else {
                            viewModel.selectSoundscape(soundscape)
                            if (!isPlaying) {
                                viewModel.toggleSoundscapePlay()
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Section: Guided Meditations
        if (selectedFilter in listOf("All", "Guided Journeys", "Stress", "Sleep")) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Guided Journeys",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = OnSurface
                    )
                    Text(
                        text = "Mindful Awakening",
                        fontFamily = InterFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Tertiary,
                        letterSpacing = 0.05.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            val filteredJourneys = DefaultGuidedMeditations.filter {
                when (selectedFilter) {
                    "Stress" -> it.category == "Stress" || it.category == "Calm"
                    "Sleep" -> it.category == "Sleep"
                    else -> true
                }
            }

            items(filteredJourneys) { meditation ->
                GuidedMeditationCard(
                    meditation = meditation,
                    onStart = {
                        viewModel.startGuidedMeditation(meditation)
                        onStartMeditation(meditation)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun SoundscapeCard(
    soundscape: SoundscapeItem,
    isPlaying: Boolean,
    onTap: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("soundscape_card_${soundscape.id}")
            .liquidGlass(
                shape = shape,
                backgroundColor = if (isPlaying) Color(0x7017253B) else Color(0x50121A28)
            )
            .clickable(onClick = onTap)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPlaying) Color(0x357EE0D2) else Color(0x18FFFFFF),
                        CircleShape
                    )
                    .border(
                        1.dp,
                        if (isPlaying) Color(0x607EE0D2) else Color(0x20FFFFFF),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (isPlaying) Primary else soundscape.tintColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = soundscape.title,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = if (isPlaying) Primary else OnSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = soundscape.subtitle,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = OnSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(Color(0x16FFFFFF))
                    .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(9999.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = soundscape.frequencyLabel,
                    fontFamily = InterFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    color = soundscape.tintColor,
                    letterSpacing = 0.04.sp
                )
            }
        }
    }
}

@Composable
fun GuidedMeditationCard(
    meditation: GuidedMeditation,
    onStart: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guided_meditation_card_${meditation.id}")
            .liquidGlass(shape = shape, backgroundColor = Color(0x65131C2D))
            .clickable(onClick = onStart)
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color(0x20CCBEFF))
                        .border(1.dp, Color(0x35CCBEFF), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = meditation.category,
                        fontFamily = InterFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = Secondary,
                        letterSpacing = 0.08.sp
                    )
                }

                Text(
                    text = "${meditation.durationMinutes} minutes",
                    fontFamily = InterFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = OnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = meditation.title,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = meditation.subtitle,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = OnSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Chimes & Atmospheric Soundscape",
                        fontFamily = InterFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        color = Primary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x337EE0D2), CircleShape)
                        .border(1.dp, Color(0x607EE0D2), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Journey",
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
