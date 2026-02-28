package com.example.kadaracompose.one.networking.presentation

import com.example.kadaracompose.one.networking.domain.model.Post

/**
 * Models every possible state the Posts screen can be in.
 *
 * Notice this is a single data class, not a sealed class.
 * The sealed class approach (Loading | Success | Error) forces the UI
 * into one state at a time — but real screens often need combinations:
 *   - Show the list AND a loading indicator for a refresh
 *   - Show the list AND an error snackbar for a failed delete
 *
 * This "state holder" approach handles those cases naturally.
 */
data class PostsState(
    // List state
    val posts: List<Post> = emptyList(),
    val isLoadingPosts: Boolean = false,
    val postsError: String? = null,

    // Create post form
    val showCreateForm: Boolean = false,
    val newPostTitle: String = "",
    val newPostBody: String = "",
    val isCreating: Boolean = false,
    val createError: String? = null,

    // Detail view
    val selectedPost: Post? = null,
    val isLoadingDetail: Boolean = false,
    val detailError: String? = null,

    // Transient feedback (snackbar)
    val userMessage: String? = null
) {
    // Computed properties — convenient for the UI
    val isFormValid: Boolean get() = newPostTitle.isNotBlank() && newPostBody.isNotBlank()
    val isEmpty: Boolean get() = posts.isEmpty() && !isLoadingPosts && postsError == null
}

sealed class PostsEvent {
    data class ShowMessage(val message: String) : PostsEvent()
    object NavigateToDetail : PostsEvent()
    object PostCreated : PostsEvent()
}
