package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LetterDao {
    @Query("SELECT * FROM letters WHERE isTemplate = 0 ORDER BY updatedAt DESC")
    fun getAllDocuments(): Flow<List<LetterEntity>>

    @Query("SELECT * FROM letters WHERE isTemplate = 0 ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentLetters(limit: Int): Flow<List<LetterEntity>>

    @Query("SELECT * FROM letters WHERE isTemplate = 1 ORDER BY updatedAt DESC")
    fun getCustomTemplates(): Flow<List<LetterEntity>>

    @Query("SELECT * FROM letters WHERE id = :id LIMIT 1")
    suspend fun getLetterById(id: Long): LetterEntity?

    @Query("SELECT * FROM letters WHERE isTemplate = 0 AND (title LIKE '%' || :query || '%' OR subject LIKE '%' || :query || '%' OR recipientDetails LIKE '%' || :query || '%') ORDER BY updatedAt DESC")
    fun searchDocuments(query: String): Flow<List<LetterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(entity: LetterEntity): Long

    @Update
    suspend fun updateLetter(entity: LetterEntity)

    @Query("DELETE FROM letters WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Delete
    suspend fun deleteLetter(entity: LetterEntity)
}
