package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.SoundscapeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundscapeEngine {
    private val sampleRate = 22050
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    private val random = Random()

    @Volatile
    var isPlaying: Boolean = false
        private set

    @Volatile
    var currentSoundscape: SoundscapeType? = null
        private set

    @Volatile
    var volume: Float = 0.65f
        set(value) {
            field = value.coerceIn(0f, 1f)
            audioTrack?.setVolume(field)
        }

    fun playSoundscape(type: SoundscapeType) {
        if (isPlaying && currentSoundscape == type) return
        stopSoundscape()
        currentSoundscape = type
        isPlaying = true

        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = maxOf(minBufferSize, sampleRate / 2)

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.setVolume(volume)
        audioTrack?.play()

        playbackJob = scope.launch {
            val shortBuffer = ShortArray(1024)
            var sampleIndex = 0L

            // Noise filter states for pink noise & wind
            var b0 = 0.0
            var b1 = 0.0
            var b2 = 0.0
            var b3 = 0.0
            var b4 = 0.0
            var b5 = 0.0
            var b6 = 0.0

            while (isActive && isPlaying) {
                for (i in shortBuffer.indices) {
                    val t = sampleIndex.toDouble() / sampleRate
                    var sampleVal = 0.0

                    // Pink noise generator (Paul Kellet algorithm)
                    val white = (random.nextDouble() * 2.0 - 1.0)
                    b0 = 0.99886 * b0 + white * 0.0555179
                    b1 = 0.99332 * b1 + white * 0.0750759
                    b2 = 0.96900 * b2 + white * 0.1538520
                    b3 = 0.86650 * b3 + white * 0.3104856
                    b4 = 0.55000 * b4 + white * 0.5329522
                    b5 = -0.7616 * b5 - white * 0.0168980
                    val pink = (b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362) * 0.11
                    b6 = white * 0.115926

                    when (currentSoundscape) {
                        SoundscapeType.ETHEREAL_TIDE -> {
                            // Ocean swell period of ~7.5 seconds
                            val swellPeriod = 7.5
                            val swellPhase = (t % swellPeriod) / swellPeriod
                            val swellEnvelope = (1.0 - kotlin.math.cos(swellPhase * 2.0 * PI)) * 0.5
                            val lowRumble = sin(2.0 * PI * 65.0 * t) * 0.2
                            sampleVal = (pink * 0.85 + lowRumble) * swellEnvelope * 0.8
                        }
                        SoundscapeType.TWILIGHT_RAIN -> {
                            // Soothing steady rain with random droplets
                            val dropletChance = random.nextDouble()
                            val droplet = if (dropletChance > 0.994) {
                                sin(2.0 * PI * (1200.0 + random.nextDouble() * 1000.0) * t) * 0.4
                            } else 0.0
                            sampleVal = (pink * 0.45 + droplet) * 0.6
                        }
                        SoundscapeType.SINGING_BOWL -> {
                            // Repeating struck Tibetan singing bowl every 6 seconds with harmonic resonance
                            val bowlPeriod = 6.0
                            val bowlTime = t % bowlPeriod
                            val decay = exp(-bowlTime * 0.85)
                            val f0 = 432.0
                            val overtone1 = 864.0
                            val overtone2 = 1296.0
                            val tremolo = 1.0 + 0.15 * sin(2.0 * PI * 1.8 * t)
                            val tone = (sin(2.0 * PI * f0 * t) * 0.6 +
                                    sin(2.0 * PI * overtone1 * t) * 0.3 +
                                    sin(2.0 * PI * overtone2 * t) * 0.15) * tremolo
                            sampleVal = tone * decay * 0.7
                        }
                        SoundscapeType.CELESTIAL_DRONE -> {
                            // 432 Hz warm binaural meditative chord with gentle shimmer
                            val f1 = 216.0
                            val f2 = 219.5 // 3.5 Hz binaural delta/theta beat
                            val f3 = 324.0
                            val f4 = 432.0
                            val chord = (sin(2.0 * PI * f1 * t) * 0.35 +
                                    sin(2.0 * PI * f2 * t) * 0.35 +
                                    sin(2.0 * PI * f3 * t) * 0.2 +
                                    sin(2.0 * PI * f4 * t) * 0.15)
                            sampleVal = chord * 0.6
                        }
                        SoundscapeType.PINE_FOREST -> {
                            // Soft wind gusting through trees
                            val windPeriod = 11.0
                            val windEnvelope = 0.3 + 0.4 * ((1.0 + sin(2.0 * PI * (t / windPeriod))) * 0.5)
                            sampleVal = pink * windEnvelope * 0.5
                        }
                        SoundscapeType.STREAM_NOCTURNE -> {
                            // Babbling mountain brook
                            val modulation = 0.5 + 0.3 * sin(2.0 * PI * 4.2 * t)
                            sampleVal = pink * modulation * 0.6
                        }
                        null -> {
                            sampleVal = 0.0
                        }
                    }

                    val clamped = (sampleVal.coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.85).toInt()
                    shortBuffer[i] = clamped.toShort()
                    sampleIndex++
                }
                audioTrack?.write(shortBuffer, 0, shortBuffer.size)
            }
        }
    }

    fun playChime() {
        scope.launch {
            try {
                val chimeTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(sampleRate * 2) // ~1 second
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                val duration = (sampleRate * 2.5).toInt()
                val chimeBuffer = ShortArray(duration)
                val f0 = 528.0 // 528 Hz Love/DNA frequency chime
                val f1 = 1056.0
                for (i in chimeBuffer.indices) {
                    val t = i.toDouble() / sampleRate
                    val decay = exp(-t * 1.6)
                    val tone = (sin(2.0 * PI * f0 * t) * 0.7 + sin(2.0 * PI * f1 * t) * 0.3) * decay
                    chimeBuffer[i] = (tone.coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.6).toInt().toShort()
                }

                chimeTrack.write(chimeBuffer, 0, chimeBuffer.size)
                chimeTrack.play()
                kotlinx.coroutines.delay(2600)
                chimeTrack.stop()
                chimeTrack.release()
            } catch (_: Exception) {
            }
        }
    }

    fun stopSoundscape() {
        isPlaying = false
        currentSoundscape = null
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {
        }
        audioTrack = null
    }

    fun release() {
        stopSoundscape()
    }
}
