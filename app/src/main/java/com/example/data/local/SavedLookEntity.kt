package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_looks")
data class SavedLookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val lookName: String,
    val occasionId: String,
    val modeId: String,
    val mainGarmentId: String,
    val innerGarmentId: String?,
    val bottomId: String,
    val footwearId: String,
    val headwearId: String?,
    val accessoryId: String?,
    val hairstyleId: String?,
    val primaryColorId: String,
    val secondaryColorId: String,
    val totalScore: Int,
    val culturalStatus: String,
    val userNotes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
