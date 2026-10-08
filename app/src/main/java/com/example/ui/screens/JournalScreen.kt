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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.MindfulQuotes
import com.example.data.MoodOptions
import com.example.data.ReflectionEntity
import com.example.ui.components.LiquidPillButton
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
fun JournalScreen(
    viewModel: MindfulnessViewModel,
    modifier: Modifier = Modifier
) {
    val reflections by viewModel.allReflections.collectAsState()
    val totalSeconds by viewModel.totalMindfulSeconds.collectAsState()
    val sessionCount by viewModel.totalSessionsCount.collectAsState()

    val totalMinutes = (totalSeconds ?: 0L) / 60

    var isAddingNew by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf(MoodOptions[0]) }
    var reflectionText by remember { mutableStateOf("") }
    var gratitudeText by remember { mutableStateOf("") }

    val dailyQuote = remember { MindfulQuotes.random() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Reflection",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Light,
                fontSize = 32.sp,
                color = OnSurface,
                letterSpacing = (-0.02).sp
            )
            Text(
                text = "Capture interior stillness & emotional resonance",
                fontFamily = InterFont,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = OnSurfaceVariant,
                letterSpacing = 0.04.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Mindful Metrics Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(shape = RoundedCornerShape(24.dp), backgroundColor = Color(0x66141D2E))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricItem(
                        value = "$totalMinutes",
                        unit = "Mindful Min",
                        icon = Icons.Default.Timer,
                        tint = Primary
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Color(0x20FFFFFF))
                    )
                    MetricItem(
                        value = "$sessionCount",
                        unit = "Completed",
                        icon = Icons.Default.SelfImprovement,
                        tint = Secondary
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Color(0x20FFFFFF))
                    )
                    MetricItem(
                        value = "${maxOf(1, sessionCount / 2 + 1)}",
                        unit = "Day Streak",
                        icon = Icons.Default.Spa,
                        tint = Tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Mindful Quote Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(shape = RoundedCornerShape(20.dp), backgroundColor = Color(0x40101826))
                    .padding(16.dp)
            ) {
                Text(
                    text = "“$dailyQuote”",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                    color = Secondary,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // New Entry Button or Expander
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Reflections",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = OnSurface
                )

                LiquidPillButton(
                    text = if (isAddingNew) "Close" else "Record Thought",
                    onClick = { isAddingNew = !isAddingNew },
                    isPrimary = !isAddingNew,
                    testTag = "toggle_new_reflection_btn"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // New Reflection Card
        if (isAddingNew) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(shape = RoundedCornerShape(24.dp), backgroundColor = Color(0x75162235))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "How does your spirit feel right now?",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = OnSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Mood Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            items(MoodOptions) { mood ->
                                val isSelected = selectedMood.name == mood.name
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(
                                            if (isSelected) Color(0x357EE0D2) else Color(0x18FFFFFF),
                                            RoundedCornerShape(9999.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) mood.color else Color(0x20FFFFFF),
                                            RoundedCornerShape(9999.dp)
                                        )
                                        .clickable { selectedMood = mood }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = mood.emoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = mood.name,
                                            fontFamily = InterFont,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = if (isSelected) mood.color else OnSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = reflectionText,
                            onValueChange = { reflectionText = it },
                            placeholder = {
                                Text(
                                    "Notice the sensations in your body and thoughts...",
                                    color = Color(0x60FFFFFF),
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("reflection_input_field"),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Color(0x30FFFFFF),
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = gratitudeText,
                            onValueChange = { gratitudeText = it },
                            placeholder = {
                                Text(
                                    "One thing I am grateful for today...",
                                    color = Color(0x60FFFFFF),
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("gratitude_input_field"),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Secondary,
                                unfocusedBorderColor = Color(0x30FFFFFF),
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LiquidPillButton(
                            text = "Save to Sanctuary",
                            onClick = {
                                if (reflectionText.isNotBlank() || gratitudeText.isNotBlank()) {
                                    viewModel.saveReflection(
                                        mood = selectedMood.name,
                                        prompt = "Mindful Check-in",
                                        notes = reflectionText,
                                        gratitude = gratitudeText
                                    )
                                    reflectionText = ""
                                    gratitudeText = ""
                                    isAddingNew = false
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            isPrimary = true,
                            testTag = "save_reflection_btn"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // List of past reflections
        if (reflections.isEmpty() && !isAddingNew) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(shape = RoundedCornerShape(20.dp), backgroundColor = Color(0x30111824))
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🌿",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your sanctuary is pristine and still",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = OnSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'Record Thought' to log your mindful moments",
                            fontFamily = InterFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(reflections) { entry ->
                ReflectionEntryCard(
                    entry = entry,
                    onDelete = { viewModel.deleteReflection(entry) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun MetricItem(
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Light,
            fontSize = 22.sp,
            color = OnSurface
        )
        Text(
            text = unit,
            fontFamily = InterFont,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            color = OnSurfaceVariant,
            letterSpacing = 0.05.sp
        )
    }
}

@Composable
fun ReflectionEntryCard(
    entry: ReflectionEntity,
    onDelete: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reflection_card_${entry.id}")
            .liquidGlass(shape = shape, backgroundColor = Color(0x55131C2D))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(Color(0x257EE0D2))
                            .border(1.dp, Color(0x407EE0D2), RoundedCornerShape(9999.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = entry.mood,
                            fontFamily = InterFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = Primary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = entry.dateString,
                        fontFamily = InterFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        color = OnSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_reflection_${entry.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Reflection",
                        tint = Color(0x60FFFFFF),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (entry.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = entry.notes,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = OnSurface,
                    lineHeight = 20.sp
                )
            }

            if (entry.gratitude.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = "Gratitude: ",
                        fontFamily = InterFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Secondary
                    )
                    Text(
                        text = entry.gratitude,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = OnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
