package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GuidedMeditation
import com.example.ui.components.LiquidAudioBar
import com.example.ui.components.SoundscapeBottomSheet
import com.example.ui.screens.BreatheScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.SessionActiveScreen
import com.example.ui.screens.TimerScreen
import com.example.ui.theme.Background
import com.example.ui.theme.InterFont
import com.example.ui.theme.LiquidGlassMindfulnessTheme
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.viewmodel.MindfulnessViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MindfulnessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LiquidGlassMindfulnessTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

enum class NavigationTab(val label: String, val icon: ImageVector) {
    BREATHE("Breathe", Icons.Default.Air),
    EXPLORE("Sanctuary", Icons.Default.Explore),
    TIMER("Timer", Icons.Default.Timer),
    REFLECT("Reflect", Icons.Default.Book)
}

@Composable
fun MainContent(viewModel: MindfulnessViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var activeMeditationSession by remember { mutableStateOf<GuidedMeditation?>(null) }

    val currentSoundscape by viewModel.currentSoundscape.collectAsState()
    val isSoundscapePlaying by viewModel.isSoundscapePlaying.collectAsState()
    val soundscapeVolume by viewModel.soundscapeVolume.collectAsState()
    val showSoundscapeSheet by viewModel.showSoundscapeSheet.collectAsState()

    // Handle back button
    BackHandler(enabled = activeMeditationSession != null || selectedTab != 0) {
        if (activeMeditationSession != null) {
            viewModel.exitGuidedMeditation()
            activeMeditationSession = null
        } else {
            selectedTab = 0
        }
    }

    if (activeMeditationSession != null) {
        SessionActiveScreen(
            meditation = activeMeditationSession!!,
            viewModel = viewModel,
            onFinish = { activeMeditationSession = null }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            containerColor = Background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Main Screen Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        Crossfade(targetState = selectedTab, label = "screen_transition") { tabIndex ->
                            when (tabIndex) {
                                0 -> BreatheScreen(viewModel = viewModel)
                                1 -> ExploreScreen(
                                    viewModel = viewModel,
                                    onStartMeditation = { meditation ->
                                        activeMeditationSession = meditation
                                    }
                                )
                                2 -> TimerScreen(viewModel = viewModel)
                                3 -> JournalScreen(viewModel = viewModel)
                            }
                        }
                    }

                    // Persistent Liquid Audio Bar (pinned above bottom bar)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        LiquidAudioBar(
                            soundscape = currentSoundscape,
                            isPlaying = isSoundscapePlaying,
                            onTogglePlay = { viewModel.toggleSoundscapePlay() },
                            onOpenSheet = { viewModel.setShowSoundscapeSheet(true) }
                        )
                    }

                    // Floating Liquid Glass Navigation Bar
                    LiquidBottomNavBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        modifier = Modifier.navigationBarsPadding()
                    )
                }

                // Soundscape Bottom Sheet
                if (showSoundscapeSheet) {
                    SoundscapeBottomSheet(
                        currentSoundscape = currentSoundscape,
                        isPlaying = isSoundscapePlaying,
                        volume = soundscapeVolume,
                        onSelectSoundscape = { viewModel.selectSoundscape(it) },
                        onVolumeChange = { viewModel.setSoundscapeVolume(it) },
                        onDismiss = { viewModel.setShowSoundscapeSheet(false) }
                    )
                }
            }
        }
    }
}

@Composable
fun LiquidBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val navShape = RoundedCornerShape(26.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(navShape)
            .background(Color(0xDB111927), navShape)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color(0x35FFFFFF),
                        Color(0x207EE0D2),
                        Color(0x15CCBEFF)
                    )
                ),
                navShape
            )
            .drawBehind {
                // Top delicate specular line
                drawLine(
                    brush = Brush.horizontalGradient(
                        0.0f to Color.Transparent,
                        0.3f to Color(0x35FFFFFF),
                        0.7f to Color(0x35FFFFFF),
                        1.0f to Color.Transparent
                    ),
                    start = Offset(20.dp.toPx(), 1.dp.toPx()),
                    end = Offset(size.width - 20.dp.toPx(), 1.dp.toPx()),
                    strokeWidth = 1.2f
                )
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationTab.values().forEachIndexed { index, tab ->
                val isSelected = selectedTab == index
                val itemShape = RoundedCornerShape(9999.dp)

                Box(
                    modifier = Modifier
                        .clip(itemShape)
                        .background(
                            if (isSelected) Color(0x307EE0D2) else Color.Transparent,
                            itemShape
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color(0x557EE0D2) else Color.Transparent,
                            itemShape
                        )
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("nav_tab_${tab.label.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = if (isSelected) Primary else OnSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = tab.label,
                                fontFamily = InterFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Primary,
                                letterSpacing = 0.04.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
