package com.example.kadaracompose.one.networking.domain.model

/**
 * The domain model — what the rest of your app knows about a Post.
 *
 * Intentionally separate from PostDto (the raw API response).
 * This means if the API changes its field names or structure,
 * only the repository mapping changes — not ViewModels, not UI.
 */
data class Post(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int
)

data class NewPost(
    val title: String,
    val body: String,
    val userId: Int = 1
)
