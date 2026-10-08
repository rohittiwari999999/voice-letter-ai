package com.example.data.repository

import com.example.data.local.LetterDao
import com.example.data.local.LetterEntity
import com.example.data.model.Letter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LetterRepository(private val letterDao: LetterDao) {

    val allDocuments: Flow<List<Letter>> = letterDao.getAllDocuments().map { list ->
        list.map { it.toLetter() }
    }

    val customTemplates: Flow<List<Letter>> = letterDao.getCustomTemplates().map { list ->
        list.map { it.toLetter() }
    }

    fun getRecentLetters(limit: Int = 5): Flow<List<Letter>> = letterDao.getRecentLetters(limit).map { list ->
        list.map { it.toLetter() }
    }

    fun searchDocuments(query: String): Flow<List<Letter>> = letterDao.searchDocuments(query).map { list ->
        list.map { it.toLetter() }
    }

    suspend fun getLetterById(id: Long): Letter? {
        return letterDao.getLetterById(id)?.toLetter()
    }

    suspend fun saveLetter(letter: Letter): Long {
        val entity = LetterEntity.fromLetter(letter.copy(updatedAt = System.currentTimeMillis()))
        return letterDao.insertLetter(entity)
    }

    suspend fun updateLetter(letter: Letter) {
        val entity = LetterEntity.fromLetter(letter.copy(updatedAt = System.currentTimeMillis()))
        letterDao.updateLetter(entity)
    }

    suspend fun deleteLetter(id: Long) {
        letterDao.deleteById(id)
    }
}
