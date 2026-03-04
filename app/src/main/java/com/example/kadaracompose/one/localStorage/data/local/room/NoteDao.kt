package com.example.kadaracompose.one.localStorage.data.local.room

import androidx.room.*
import com.example.kadaracompose.one.localStorage.domain.model.Note
import kotlinx.coroutines.flow.Flow

// ── Entity ────────────────────────────────────────────────────────────────────

/**
 * Room entity — this is how a Note looks in the database.
 * Kept in the data layer, separate from the domain model.
 *
 * @Entity maps to a table. @PrimaryKey with autoGenerate means
 * Room assigns IDs automatically on insert.
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val body: String,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Note = Note(
        id = id,
        title = title,
        body = body,
        isPinned = isPinned,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(note: Note): NoteEntity = NoteEntity(
            id = note.id,
            title = note.title,
            body = note.body,
            isPinned = note.isPinned,
            createdAt = note.createdAt
        )
    }
}

// ── DAO ───────────────────────────────────────────────────────────────────────

/**
 * Data Access Object — all SQL lives here, nowhere else.
 *
 * Key thing to study: read operations return Flow<T>.
 * This means Room watches the table and re-emits whenever
 * data changes. The UI never has to manually refresh.
 *
 * Write operations are suspend — they must run off the main thread.
 */
@Dao
interface NoteDao {

    // Pinned notes first, then by newest — ORDER BY with multiple columns
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, createdAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE isPinned = 1 ORDER BY createdAt DESC")
    fun getPinnedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT COUNT(*) FROM notes")
    fun getNoteCount(): Flow<Int>

    /**
     * REPLACE strategy: if a note with the same primary key exists,
     * replace it. Otherwise insert a new row.
     * This lets you use one method for both insert and update.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()
}
