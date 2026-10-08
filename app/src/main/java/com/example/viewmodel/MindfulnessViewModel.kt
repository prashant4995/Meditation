package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundscapeEngine
import com.example.data.BreathPhase
import com.example.data.BreathingPattern
import com.example.data.CompletedSessionEntity
import com.example.data.DefaultBreathingPatterns
import com.example.data.DefaultGuidedMeditations
import com.example.data.DefaultSoundscapes
import com.example.data.GuidedMeditation
import com.example.data.MindfulnessDatabase
import com.example.data.ReflectionEntity
import com.example.data.SoundscapeItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MindfulnessViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MindfulnessDatabase.getDatabase(application)
    private val dao = database.reflectionDao()
    val soundEngine = SoundscapeEngine()

    // -----------------------
    // Haptic feedback manager
    // -----------------------
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private fun triggerGentleHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(35)
            }
        } catch (_: Exception) {}
    }

    // -----------------------
    // Soundscape State
    // -----------------------
    private val _currentSoundscape = MutableStateFlow<SoundscapeItem?>(DefaultSoundscapes[0])
    val currentSoundscape: StateFlow<SoundscapeItem?> = _currentSoundscape.asStateFlow()

    private val _isSoundscapePlaying = MutableStateFlow(false)
    val isSoundscapePlaying: StateFlow<Boolean> = _isSoundscapePlaying.asStateFlow()

    private val _soundscapeVolume = MutableStateFlow(0.65f)
    val soundscapeVolume: StateFlow<Float> = _soundscapeVolume.asStateFlow()

    private val _showSoundscapeSheet = MutableStateFlow(false)
    val showSoundscapeSheet: StateFlow<Boolean> = _showSoundscapeSheet.asStateFlow()

    fun setShowSoundscapeSheet(show: Boolean) {
        _showSoundscapeSheet.value = show
    }

    fun selectSoundscape(item: SoundscapeItem) {
        _currentSoundscape.value = item
        if (_isSoundscapePlaying.value) {
            soundEngine.playSoundscape(item.type)
        }
    }

    fun toggleSoundscapePlay() {
        val current = _currentSoundscape.value ?: DefaultSoundscapes[0]
        if (_isSoundscapePlaying.value) {
            soundEngine.stopSoundscape()
            _isSoundscapePlaying.value = false
        } else {
            soundEngine.playSoundscape(current.type)
            _isSoundscapePlaying.value = true
        }
    }

    fun setSoundscapeVolume(vol: Float) {
        _soundscapeVolume.value = vol
        soundEngine.volume = vol
    }

    // -----------------------
    // Breathing Guide Orb State
    // -----------------------
    private val _selectedBreathingPattern = MutableStateFlow(DefaultBreathingPatterns[0])
    val selectedBreathingPattern: StateFlow<BreathingPattern> = _selectedBreathingPattern.asStateFlow()

    private val _isBreathingActive = MutableStateFlow(false)
    val isBreathingActive: StateFlow<Boolean> = _isBreathingActive.asStateFlow()

    private val _currentBreathPhase = MutableStateFlow(BreathPhase.INHALE)
    val currentBreathPhase: StateFlow<BreathPhase> = _currentBreathPhase.asStateFlow()

    private val _phaseProgress = MutableStateFlow(0f)
    val phaseProgress: StateFlow<Float> = _phaseProgress.asStateFlow()

    private val _phaseSecondsRemaining = MutableStateFlow(4.0f)
    val phaseSecondsRemaining: StateFlow<Float> = _phaseSecondsRemaining.asStateFlow()

    private val _completedBreathCycles = MutableStateFlow(0)
    val completedBreathCycles: StateFlow<Int> = _completedBreathCycles.asStateFlow()

    private val _totalBreathingElapsedSeconds = MutableStateFlow(0)
    val totalBreathingElapsedSeconds: StateFlow<Int> = _totalBreathingElapsedSeconds.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(true)
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    fun toggleHaptic() {
        _hapticEnabled.value = !_hapticEnabled.value
    }

    private var breathingJob: Job? = null

    fun selectBreathingPattern(pattern: BreathingPattern) {
        _selectedBreathingPattern.value = pattern
        if (_isBreathingActive.value) {
            stopBreathing()
            startBreathing()
        } else {
            _phaseSecondsRemaining.value = pattern.inhaleSec
            _phaseProgress.value = 0f
            _currentBreathPhase.value = BreathPhase.INHALE
        }
    }

    fun toggleBreathing() {
        if (_isBreathingActive.value) {
            stopBreathing()
        } else {
            startBreathing()
        }
    }

    private fun startBreathing() {
        _isBreathingActive.value = true
        val pattern = _selectedBreathingPattern.value

        breathingJob = viewModelScope.launch {
            if (_hapticEnabled.value) triggerGentleHaptic()
            soundEngine.playChime()

            while (_isBreathingActive.value) {
                // Phase 1: Inhale
                runPhase(BreathPhase.INHALE, pattern.inhaleSec)
                if (!_isBreathingActive.value) break

                // Phase 2: Hold In (if duration > 0)
                if (pattern.holdInSec > 0f) {
                    runPhase(BreathPhase.HOLD_IN, pattern.holdInSec)
                    if (!_isBreathingActive.value) break
                }

                // Phase 3: Exhale
                runPhase(BreathPhase.EXHALE, pattern.exhaleSec)
                if (!_isBreathingActive.value) break

                // Phase 4: Hold Out (if duration > 0)
                if (pattern.holdOutSec > 0f) {
                    runPhase(BreathPhase.HOLD_OUT, pattern.holdOutSec)
                    if (!_isBreathingActive.value) break
                }

                _completedBreathCycles.value += 1
            }
        }
    }

    private suspend fun runPhase(phase: BreathPhase, durationSec: Float) {
        _currentBreathPhase.value = phase
        if (_hapticEnabled.value) triggerGentleHaptic()

        val stepMs = 50L
        val totalSteps = (durationSec * 1000 / stepMs).toInt()

        for (step in 0..totalSteps) {
            if (!_isBreathingActive.value) break
            val progress = step.toFloat() / totalSteps.toFloat()
            val remaining = (durationSec * (1f - progress)).coerceAtLeast(0f)
            _phaseProgress.value = progress
            _phaseSecondsRemaining.value = remaining
            delay(stepMs)
            if (step % 20 == 0) {
                _totalBreathingElapsedSeconds.value += 1
            }
        }
    }

    fun stopBreathing() {
        _isBreathingActive.value = false
        breathingJob?.cancel()
        breathingJob = null

        val pattern = _selectedBreathingPattern.value
        _phaseProgress.value = 0f
        _phaseSecondsRemaining.value = pattern.inhaleSec
        _currentBreathPhase.value = BreathPhase.INHALE

        // Record completed session if user did at least 30 seconds
        val seconds = _totalBreathingElapsedSeconds.value
        if (seconds >= 30) {
            viewModelScope.launch {
                dao.insertCompletedSession(
                    CompletedSessionEntity(
                        sessionType = "Breathwork",
                        title = pattern.name,
                        durationSeconds = seconds
                    )
                )
            }
        }
    }

    // -----------------------
    // Custom Zen Timer State
    // -----------------------
    private val _timerTargetMinutes = MutableStateFlow(5)
    val timerTargetMinutes: StateFlow<Int> = _timerTargetMinutes.asStateFlow()

    private val _timerRemainingSeconds = MutableStateFlow(300)
    val timerRemainingSeconds: StateFlow<Int> = _timerRemainingSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _timerIntention = MutableStateFlow("Return to effortless awareness")
    val timerIntention: StateFlow<String> = _timerIntention.asStateFlow()

    private val _intervalBellMinutes = MutableStateFlow(0) // 0 = none, 2 = 2m, 5 = 5m
    val intervalBellMinutes: StateFlow<Int> = _intervalBellMinutes.asStateFlow()

    private var timerJob: Job? = null

    fun setTimerTargetMinutes(min: Int) {
        _timerTargetMinutes.value = min
        if (!_isTimerRunning.value) {
            _timerRemainingSeconds.value = min * 60
        }
    }

    fun setTimerIntention(intention: String) {
        _timerIntention.value = intention
    }

    fun setIntervalBellMinutes(interval: Int) {
        _intervalBellMinutes.value = interval
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        soundEngine.playChime()
        if (_hapticEnabled.value) triggerGentleHaptic()

        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value && _timerRemainingSeconds.value > 0) {
                delay(1000L)
                _timerRemainingSeconds.value -= 1

                val elapsed = (_timerTargetMinutes.value * 60) - _timerRemainingSeconds.value
                val interval = _intervalBellMinutes.value
                if (interval > 0 && elapsed > 0 && elapsed % (interval * 60) == 0 && _timerRemainingSeconds.value > 0) {
                    soundEngine.playChime()
                }

                if (_timerRemainingSeconds.value <= 0) {
                    soundEngine.playChime()
                    if (_hapticEnabled.value) triggerGentleHaptic()
                    _isTimerRunning.value = false
                    dao.insertCompletedSession(
                        CompletedSessionEntity(
                            sessionType = "Zen Timer",
                            title = "${_timerTargetMinutes.value}m Meditation",
                            durationSeconds = _timerTargetMinutes.value * 60
                        )
                    )
                    break
                }
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        pauseTimer()
        _timerRemainingSeconds.value = _timerTargetMinutes.value * 60
    }

    // -----------------------
    // Guided Meditations State
    // -----------------------
    private val _activeMeditation = MutableStateFlow<GuidedMeditation?>(null)
    val activeMeditation: StateFlow<GuidedMeditation?> = _activeMeditation.asStateFlow()

    private val _meditationPromptIndex = MutableStateFlow(0)
    val meditationPromptIndex: StateFlow<Int> = _meditationPromptIndex.asStateFlow()

    private val _meditationElapsedSeconds = MutableStateFlow(0)
    val meditationElapsedSeconds: StateFlow<Int> = _meditationElapsedSeconds.asStateFlow()

    private var guidedJob: Job? = null

    fun startGuidedMeditation(meditation: GuidedMeditation) {
        _activeMeditation.value = meditation
        _meditationPromptIndex.value = 0
        _meditationElapsedSeconds.value = 0

        // Play appropriate soundscape automatically
        val matchingSoundscape = DefaultSoundscapes.find { it.type == meditation.soundscapeType }
        if (matchingSoundscape != null) {
            selectSoundscape(matchingSoundscape)
            if (!_isSoundscapePlaying.value) {
                toggleSoundscapePlay()
            }
        }
        soundEngine.playChime()

        guidedJob?.cancel()
        guidedJob = viewModelScope.launch {
            val totalPrompts = meditation.guidePrompts.size
            val promptInterval = (meditation.durationMinutes * 60) / maxOf(1, totalPrompts)

            while (_activeMeditation.value != null && _meditationElapsedSeconds.value < meditation.durationMinutes * 60) {
                delay(1000L)
                _meditationElapsedSeconds.value += 1

                val newIndex = (_meditationElapsedSeconds.value / promptInterval).coerceIn(0, totalPrompts - 1)
                if (newIndex != _meditationPromptIndex.value) {
                    _meditationPromptIndex.value = newIndex
                    if (_hapticEnabled.value) triggerGentleHaptic()
                }
            }

            // Finished
            soundEngine.playChime()
            dao.insertCompletedSession(
                CompletedSessionEntity(
                    sessionType = "Guided Journey",
                    title = meditation.title,
                    durationSeconds = meditation.durationMinutes * 60
                )
            )
        }
    }

    fun exitGuidedMeditation() {
        guidedJob?.cancel()
        guidedJob = null
        _activeMeditation.value = null
    }

    // -----------------------
    // Reflections & Stats
    // -----------------------
    val allReflections: StateFlow<List<ReflectionEntity>> = dao.getAllReflections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalMindfulSeconds: StateFlow<Long?> = dao.getTotalMindfulSeconds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalSessionsCount: StateFlow<Int> = dao.getTotalCompletedSessionsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun saveReflection(
        mood: String,
        prompt: String,
        notes: String,
        gratitude: String
    ) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
            val dateStr = dateFormat.format(Date())

            val reflection = ReflectionEntity(
                dateString = dateStr,
                mood = mood,
                prompt = prompt,
                notes = notes,
                gratitude = gratitude,
                sessionType = "Reflection"
            )
            dao.insertReflection(reflection)
        }
    }

    fun deleteReflection(reflection: ReflectionEntity) {
        viewModelScope.launch {
            dao.deleteReflection(reflection)
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundEngine.release()
        breathingJob?.cancel()
        timerJob?.cancel()
        guidedJob?.cancel()
    }
}
