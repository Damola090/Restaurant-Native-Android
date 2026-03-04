package com.example.kadaracompose.one.localStorage.domain.model

/**
 * Domain model for the Room experiment.
 * Clean, no Room annotations — those only live in the data layer.
 */
data class Note(
    val id: Int = 0,
    val title: String,
    val body: String,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
