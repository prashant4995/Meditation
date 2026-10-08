package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ReflectionEntity::class, CompletedSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MindfulnessDatabase : RoomDatabase() {
    abstract fun reflectionDao(): ReflectionDao

    companion object {
        @Volatile
        private var INSTANCE: MindfulnessDatabase? = null

        fun getDatabase(context: Context): MindfulnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MindfulnessDatabase::class.java,
                    "mindfulness_liquid_glass.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
