package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLookDao {
    @Query("SELECT * FROM saved_looks ORDER BY timestamp DESC")
    fun getAllSavedLooks(): Flow<List<SavedLookEntity>>

    @Query("SELECT * FROM saved_looks WHERE id = :id LIMIT 1")
    suspend fun getLookById(id: Long): SavedLookEntity?

    @Query("SELECT * FROM saved_looks WHERE occasionId = :occasionId ORDER BY timestamp DESC")
    fun getLooksByOccasion(occasionId: String): Flow<List<SavedLookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLook(look: SavedLookEntity): Long

    @Query("DELETE FROM saved_looks WHERE id = :id")
    suspend fun deleteLookById(id: Long)

    @Query("DELETE FROM saved_looks")
    suspend fun deleteAllLooks()
}
