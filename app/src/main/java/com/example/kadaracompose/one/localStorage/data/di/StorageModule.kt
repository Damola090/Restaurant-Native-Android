package com.example.kadaracompose.one.localStorage.data.di

import android.content.Context
import androidx.room.Room
import com.example.kadaracompose.one.localStorage.data.local.datastore.UserPreferencesDataSource
import com.example.kadaracompose.one.localStorage.data.local.room.NoteDao
import com.example.kadaracompose.one.localStorage.data.local.room.StorageDatabase
import com.example.kadaracompose.one.localStorage.data.repository.NoteRepositoryImpl
import com.example.kadaracompose.one.localStorage.data.repository.PreferencesRepositoryImpl
import com.example.kadaracompose.one.localStorage.domain.repository.NoteRepository
import com.example.kadaracompose.one.localStorage.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideStorageDatabase(
        @ApplicationContext context: Context
    ): StorageDatabase {
        return Room.databaseBuilder(
            context,
            StorageDatabase::class.java,
            "storage_experiment.db"
        )
            // In production you'd write proper migrations.
            // For sandbox experimentation, destructive migration is fine.
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: StorageDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesDataSource(
        @ApplicationContext context: Context
    ): UserPreferencesDataSource {
        return UserPreferencesDataSource(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageBindingModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        impl: NoteRepositoryImpl
    ): NoteRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository
}
