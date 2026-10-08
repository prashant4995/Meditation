package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reflections")
data class ReflectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String,
    val mood: String,
    val prompt: String,
    val notes: String,
    val gratitude: String = "",
    val sessionDurationMinutes: Int = 0,
    val sessionType: String = "Reflection"
)

@Entity(tableName = "completed_sessions")
data class CompletedSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionType: String,
    val title: String,
    val durationSeconds: Int
)
