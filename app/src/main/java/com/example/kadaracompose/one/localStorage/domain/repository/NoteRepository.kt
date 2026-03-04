package com.example.kadaracompose.one.localStorage.domain.repository

import com.example.kadaracompose.one.localStorage.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Notice all read operations return Flow<T> not suspend T.
 *
 * This is the key insight of Room + Flow:
 * The UI subscribes once and Room pushes updates automatically
 * whenever the underlying table changes. You never have to
 * manually re-fetch after an insert, update or delete.
 */
interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getNoteById(id: Int): Flow<Note?>
    fun getPinnedNotes(): Flow<List<Note>>
    suspend fun insertNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun deleteAllNotes()
    suspend fun togglePin(note: Note)
}