package com.example.kadaracompose.one.localStorage.domain.usecase

import com.example.kadaracompose.one.localStorage.domain.model.Note
import com.example.kadaracompose.one.localStorage.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> = repository.getAllNotes()
}

class GetNoteByIdUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(id: Int): Flow<Note?> = repository.getNoteById(id)
}

class InsertNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        require(note.title.isNotBlank()) { "Title cannot be empty" }
        repository.insertNote(note)
    }
}

class UpdateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        require(note.title.isNotBlank()) { "Title cannot be empty" }
        repository.updateNote(note)
    }
}

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) = repository.deleteNote(note)
}

class TogglePinUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) = repository.togglePin(note)
}
