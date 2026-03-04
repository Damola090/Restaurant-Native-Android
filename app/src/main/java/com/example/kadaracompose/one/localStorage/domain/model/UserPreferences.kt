package com.example.kadaracompose.one.localStorage.domain.model

/**
 * Domain model for the DataStore experiment.
 * Represents typed user preferences — no serialization details here.
 */
data class UserPreferences(
    val displayName: String = "",
    val isDarkTheme: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val notificationsEnabled: Boolean = true
)

enum class FontSize(val label: String, val scale: Float) {
    SMALL("Small", 0.85f),
    MEDIUM("Medium", 1.0f),
    LARGE("Large", 1.2f)
}
