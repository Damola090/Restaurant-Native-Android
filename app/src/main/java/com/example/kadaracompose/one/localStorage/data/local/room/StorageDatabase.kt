package com.example.kadaracompose.one.localStorage.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The database definition. Keep this class thin —
 * it's just a registry of entities and DAOs.
 *
 * version: bump this whenever you change the schema.
 * exportSchema: true in production so you can track migrations in version control.
 *
 * The actual Room.databaseBuilder() call lives in the DI module,
 * not here — keeps this class clean and testable.
 */
@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StorageDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
