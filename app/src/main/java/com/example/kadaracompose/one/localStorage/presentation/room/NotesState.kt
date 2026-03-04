package com.example.kadaracompose.one.localStorage.presentation.room

import com.example.kadaracompose.one.localStorage.domain.model.Note

data class NotesState(
    val notes: List<Note> = emptyList(),
    val showForm: Boolean = false,
    val editingNote: Note? = null,     // null = creating new, non-null = editing existing
    val titleInput: String = "",
    val bodyInput: String = "",
    val userMessage: String? = null
) {
    val isEditing: Boolean get() = editingNote != null
    val isFormValid: Boolean get() = titleInput.isNotBlank()
    val pinnedNotes: List<Note> get() = notes.filter { it.isPinned }
    val unpinnedNotes: List<Note> get() = notes.filter { !it.isPinned }
}
