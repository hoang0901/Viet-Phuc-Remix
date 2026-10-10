package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedLookEntity::class], version = 1, exportSchema = false)
abstract class VietPhucDatabase : RoomDatabase() {
    abstract fun savedLookDao(): SavedLookDao

    companion object {
        @Volatile
        private var INSTANCE: VietPhucDatabase? = null

        fun getInstance(context: Context): VietPhucDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VietPhucDatabase::class.java,
                    "viet_phuc_remix.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
