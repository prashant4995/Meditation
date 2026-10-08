package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.Tertiary

enum class BreathPhase(val label: String, val subtitle: String) {
    INHALE("Inhale", "Breathe in deeply through your nose"),
    HOLD_IN("Hold", "Rest gently in the fullness"),
    EXHALE("Exhale", "Release slowly through softened lips"),
    HOLD_OUT("Rest", "Savor the quiet void before the next wave")
}

data class BreathingPattern(
    val id: String,
    val name: String,
    val tag: String,
    val description: String,
    val inhaleSec: Float,
    val holdInSec: Float,
    val exhaleSec: Float,
    val holdOutSec: Float,
    val accentColor: Color = Primary
) {
    val totalCycleSec: Float get() = inhaleSec + holdInSec + exhaleSec + holdOutSec
}

val DefaultBreathingPatterns = listOf(
    BreathingPattern(
        id = "box",
        name = "Box Breathing",
        tag = "Focus & Equilibrium",
        description = "Balanced 4-4-4-4 cadence used by performers to regain mental clarity under pressure.",
        inhaleSec = 4f,
        holdInSec = 4f,
        exhaleSec = 4f,
        holdOutSec = 4f,
        accentColor = Primary
    ),
    BreathingPattern(
        id = "478",
        name = "4-7-8 Relaxing",
        tag = "Sleep & De-stress",
        description = "Natural tranquilizer for the nervous system, downshifting sympathetic activation.",
        inhaleSec = 4f,
        holdInSec = 7f,
        exhaleSec = 8f,
        holdOutSec = 0f,
        accentColor = Secondary
    ),
    BreathingPattern(
        id = "resonance",
        name = "Resonance Coherence",
        tag = "Heart-Brain Sync",
        description = "5.5s equal rhythm tuning heart rate variability (HRV) into natural vagal harmony.",
        inhaleSec = 5.5f,
        holdInSec = 0f,
        exhaleSec = 5.5f,
        holdOutSec = 0f,
        accentColor = Primary
    ),
    BreathingPattern(
        id = "deep_calm",
        name = "Deep Calm",
        tag = "Anxiety Release",
        description = "Extended exhale triggers parasympathetic vagal stimulation for rapid grounding.",
        inhaleSec = 5f,
        holdInSec = 2f,
        exhaleSec = 7f,
        holdOutSec = 1f,
        accentColor = Tertiary
    ),
    BreathingPattern(
        id = "awakening",
        name = "Morning Glow",
        tag = "Gentle Energizing",
        description = "Brisk invigorating cycle clearing morning brain fog with rich oxygenation.",
        inhaleSec = 4f,
        holdInSec = 1f,
        exhaleSec = 3f,
        holdOutSec = 0f,
        accentColor = Primary
    )
)

enum class SoundscapeType {
    TWILIGHT_RAIN,
    ETHEREAL_TIDE,
    SINGING_BOWL,
    CELESTIAL_DRONE,
    PINE_FOREST,
    STREAM_NOCTURNE
}

data class SoundscapeItem(
    val id: String,
    val type: SoundscapeType,
    val title: String,
    val subtitle: String,
    val frequencyLabel: String,
    val category: String,
    val tintColor: Color
)

val DefaultSoundscapes = listOf(
    SoundscapeItem(
        id = "tide",
        type = SoundscapeType.ETHEREAL_TIDE,
        title = "Ethereal Tide",
        subtitle = "Rolling twilight ocean swell",
        frequencyLabel = "0.1 Hz Vagal Waves",
        category = "Relaxation",
        tintColor = Primary
    ),
    SoundscapeItem(
        id = "rain",
        type = SoundscapeType.TWILIGHT_RAIN,
        title = "Twilight Rain",
        subtitle = "Gentle nocturnal glass patter",
        frequencyLabel = "Pink Noise Spectrum",
        category = "Sleep & Focus",
        tintColor = Secondary
    ),
    SoundscapeItem(
        id = "bowl",
        type = SoundscapeType.SINGING_BOWL,
        title = "Tibetan Singing Bowl",
        subtitle = "Harmonic bell resonance rings",
        frequencyLabel = "432 Hz Pure Resonance",
        category = "Meditation",
        tintColor = Tertiary
    ),
    SoundscapeItem(
        id = "drone",
        type = SoundscapeType.CELESTIAL_DRONE,
        title = "Celestial Drone",
        subtitle = "Deep cosmic ambient warm chord",
        frequencyLabel = "Theta Binaural Flow",
        category = "Deep Calm",
        tintColor = Primary
    ),
    SoundscapeItem(
        id = "forest",
        type = SoundscapeType.PINE_FOREST,
        title = "Pine Forest Mist",
        subtitle = "Soft alpine breeze and distant leaves",
        frequencyLabel = "Organic Canopy Ambiance",
        category = "Grounding",
        tintColor = Secondary
    ),
    SoundscapeItem(
        id = "stream",
        type = SoundscapeType.STREAM_NOCTURNE,
        title = "Stream Nocturne",
        subtitle = "Crystal mountain creek murmur",
        frequencyLabel = "Continuous Flow",
        category = "Clarity",
        tintColor = Tertiary
    )
)

data class GuidedMeditation(
    val id: String,
    val title: String,
    val subtitle: String,
    val durationMinutes: Int,
    val category: String,
    val soundscapeType: SoundscapeType,
    val guidePrompts: List<String>,
    val accentColor: Color
)

val DefaultGuidedMeditations = listOf(
    GuidedMeditation(
        id = "morning_clarity",
        title = "Dawn Awakening",
        subtitle = "Set tranquil intentions for the daylight ahead",
        durationMinutes = 5,
        category = "Morning",
        soundscapeType = SoundscapeType.CELESTIAL_DRONE,
        guidePrompts = listOf(
            "Notice the gentle weight of your body meeting the surface below.",
            "Allow the morning air to fill your chest like light touching calm water.",
            "Release any anticipation of today's demands. This moment is sufficient.",
            "Choose one gentle word to guide your daylight journey: peace, presence, or ease.",
            "Gently soften your gaze and step into your morning with weightless grace."
        ),
        accentColor = Primary
    ),
    GuidedMeditation(
        id = "unburden",
        title = "Liquid Letting Go",
        subtitle = "Dissolve accumulated tension and emotional friction",
        durationMinutes = 10,
        category = "Stress",
        soundscapeType = SoundscapeType.TWILIGHT_RAIN,
        guidePrompts = listOf(
            "Visualize your thoughts as droplets landing upon still water.",
            "You do not need to hold or fix anything right now.",
            "With each breath, feel tight shoulders and clenched jaw soften like warm glass.",
            "Notice how thoughts arise, ripple across awareness, and dissolve back into stillness.",
            "Rest in the unshakeable calm that exists underneath all passing storms."
        ),
        accentColor = Secondary
    ),
    GuidedMeditation(
        id = "twilight_unwind",
        title = "Twilight Body Sanctuary",
        subtitle = "Progressive release drifting into nocturnal rest",
        durationMinutes = 15,
        category = "Sleep",
        soundscapeType = SoundscapeType.ETHEREAL_TIDE,
        guidePrompts = listOf(
            "Welcome the twilight. The efforts of today are completed.",
            "Bring soft awareness to the soles of your feet, letting all heaviness sink down.",
            "Softly breathe into your belly, allowing it to rise and fall without restriction.",
            "Feel the space around your eyes, forehead, and throat relax into open stillness.",
            "Surrender to the soothing cadence of the tides. You are held, safe, and at rest."
        ),
        accentColor = Tertiary
    ),
    GuidedMeditation(
        id = "anxiety_dissolve",
        title = "Sensory Anchor",
        subtitle = "Quick vagal reset when overwhelmed by noise",
        durationMinutes = 7,
        category = "Calm",
        soundscapeType = SoundscapeType.SINGING_BOWL,
        guidePrompts = listOf(
            "Place one hand gently over your heart or abdomen. Feel the steady warmth.",
            "Notice three sounds in your environment without judging or naming them.",
            "Extend your exhale longer than your inhale to signal physical safety to your body.",
            "You are not your anxious thoughts. You are the peaceful open awareness beholding them.",
            "Breathe in spacious calm; exhale gratitude for your resilient heart."
        ),
        accentColor = Primary
    )
)

data class MoodItem(
    val name: String,
    val emoji: String,
    val description: String,
    val color: Color
)

val MoodOptions = listOf(
    MoodItem("Tranquil", "🌊", "Deep calm & open ease", Primary),
    MoodItem("Grounded", "🌿", "Rooted, stable, centered", Color(0xFF75D7C9)),
    MoodItem("Reflective", "🌙", "Introspective & quiet", Secondary),
    MoodItem("Restorative", "✨", "Recharging & healing", Tertiary),
    MoodItem("Restless", "⚡", "Seeking stillness & release", Color(0xFFFFB4AB))
)

val MindfulQuotes = listOf(
    "Notice how the surface softens when the breath slows.",
    "Allow awareness to settle like morning mist upon calm water.",
    "Each inhale gathers light; each exhale releases weight.",
    "Stillness is already present underneath every movement.",
    "Let your attention rest here, clear and unhurried."
)
