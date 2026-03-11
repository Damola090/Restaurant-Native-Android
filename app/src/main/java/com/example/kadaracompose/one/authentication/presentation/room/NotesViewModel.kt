package com.example.kadaracompose.one.authentication.presentation.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kadaracompose.one.localStorage.domain.model.Note
import com.example.kadaracompose.one.localStorage.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotes: GetAllNotesUseCase,
    private val insertNote: InsertNoteUseCase,
    private val updateNote: UpdateNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
    private val togglePin: TogglePinUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NotesState())
    val state = _state.asStateFlow()

    init {
        // Collect the Flow from Room — this stays active for the ViewModel's lifetime.
        // Every insert/update/delete automatically triggers a new emission here.
        // This is the core lesson: you never manually re-fetch.
        getAllNotes()
            .onEach { notes -> _state.update { it.copy(notes = notes) } }
            .launchIn(viewModelScope)
    }

    fun onSaveNote() {
        val current = _state.value
        if (!current.isFormValid) return

        viewModelScope.launch {
            if (current.isEditing) {
                // Update existing note
                updateNote(
                    current.editingNote!!.copy(
                        title = current.titleInput,
                        body = current.bodyInput
                    )
                )
                _state.update { it.copy(userMessage = "Note updated") }
            } else {
                // Insert new note
                insertNote(Note(title = current.titleInput, body = current.bodyInput))
                _state.update { it.copy(userMessage = "Note saved") }
            }
            dismissForm()
        }
    }

    fun onDeleteNote(note: Note) {
        viewModelScope.launch {
            deleteNote(note)
            _state.update { it.copy(userMessage = "Note deleted") }
        }
    }

    fun onTogglePin(note: Note) {
        viewModelScope.launch { togglePin(note) }
    }

    fun onEditNote(note: Note) {
        _state.update {
            it.copy(
                showForm = true,
                editingNote = note,
                titleInput = note.title,
                bodyInput = note.body
            )
        }
    }

    fun onShowNewNoteForm() {
        _state.update {
            it.copy(showForm = true, editingNote = null, titleInput = "", bodyInput = "")
        }
    }

    fun onTitleChanged(value: String) = _state.update { it.copy(titleInput = value) }
    fun onBodyChanged(value: String) = _state.update { it.copy(bodyInput = value) }
    fun onMessageShown() = _state.update { it.copy(userMessage = null) }
    fun dismissForm() = _state.update {
        it.copy(showForm = false, editingNote = null, titleInput = "", bodyInput = "")
    }
}
