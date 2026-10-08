package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReflectionDao {
    @Query("SELECT * FROM reflections ORDER BY timestamp DESC")
    fun getAllReflections(): Flow<List<ReflectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: ReflectionEntity): Long

    @Delete
    suspend fun deleteReflection(reflection: ReflectionEntity)

    @Query("SELECT * FROM completed_sessions ORDER BY timestamp DESC")
    fun getAllCompletedSessions(): Flow<List<CompletedSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedSession(session: CompletedSessionEntity): Long

    @Query("SELECT SUM(durationSeconds) FROM completed_sessions")
    fun getTotalMindfulSeconds(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM completed_sessions")
    fun getTotalCompletedSessionsCount(): Flow<Int>
}
