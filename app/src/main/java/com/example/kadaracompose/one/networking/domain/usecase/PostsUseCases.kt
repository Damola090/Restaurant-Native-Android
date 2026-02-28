package com.example.kadaracompose.one.networking.domain.usecase

import com.example.kadaracompose.one.networking.data.repository.PostsRepository
import com.example.kadaracompose.one.networking.domain.model.NetworkResult
import com.example.kadaracompose.one.networking.domain.model.NewPost
import com.example.kadaracompose.one.networking.domain.model.Post
//import com.example.kadaracompose.one.networking.data.repository.PostsRepository
import javax.inject.Inject

/**
 * Use cases are single-responsibility wrappers around repository calls.
 *
 * They're the right place for business logic like:
 *   - Validation before calling the API
 *   - Combining multiple repository calls
 *   - Transforming / filtering data for a specific screen's needs
 *
 * Keep them thin if there's no real logic — that's fine too.
 * Don't add complexity just for the sake of the pattern.
 */
class GetPostsUseCase @Inject constructor(
    private val repository: PostsRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Post>> {
        return repository.getPosts()
    }
}

class GetPostByIdUseCase @Inject constructor(
    private val repository: PostsRepository
) {
    suspend operator fun invoke(id: Int): NetworkResult<Post> {
        if (id <= 0) return NetworkResult.Error("Invalid post ID")
        return repository.getPostById(id)
    }
}

class CreatePostUseCase @Inject constructor(
    private val repository: PostsRepository
) {
    suspend operator fun invoke(newPost: NewPost): NetworkResult<Post> {
        // Validation lives here, not in the ViewModel or repository
        if (newPost.title.isBlank()) {
            return NetworkResult.Error("Title cannot be empty")
        }
        if (newPost.body.isBlank()) {
            return NetworkResult.Error("Body cannot be empty")
        }
        if (newPost.title.length > 200) {
            return NetworkResult.Error("Title is too long (max 200 characters)")
        }
        return repository.createPost(newPost)
    }
}

class DeletePostUseCase @Inject constructor(
    private val repository: PostsRepository
) {
    suspend operator fun invoke(id: Int): NetworkResult<Unit> {
        return repository.deletePost(id)
    }
}
