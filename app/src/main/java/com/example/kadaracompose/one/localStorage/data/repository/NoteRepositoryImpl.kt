package com.example.kadaracompose.one.localStorage.data.repository

import com.example.kadaracompose.one.localStorage.data.local.room.NoteDao
import com.example.kadaracompose.one.localStorage.data.local.room.NoteEntity
import com.example.kadaracompose.one.localStorage.domain.model.Note
import com.example.kadaracompose.one.localStorage.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao
) : NoteRepository {

    // Map List<NoteEntity> → List<Note> inside the Flow using .map{}
    // The Flow itself stays alive — Room re-emits on every table change
    override fun getAllNotes(): Flow<List<Note>> =
        dao.getAllNotes().map { entities -> entities.map { it.toDomain() } }

    override fun getNoteById(id: Int): Flow<Note?> =
        dao.getNoteById(id).map { it?.toDomain() }

    override fun getPinnedNotes(): Flow<List<Note>> =
        dao.getPinnedNotes().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertNote(note: Note) =
        dao.insertNote(NoteEntity.fromDomain(note))

    override suspend fun updateNote(note: Note) =
        dao.updateNote(NoteEntity.fromDomain(note))

    override suspend fun deleteNote(note: Note) =
        dao.deleteNote(NoteEntity.fromDomain(note))

    override suspend fun deleteAllNotes() =
        dao.deleteAllNotes()

    override suspend fun togglePin(note: Note) =
        dao.updateNote(NoteEntity.fromDomain(note.copy(isPinned = !note.isPinned)))
}
