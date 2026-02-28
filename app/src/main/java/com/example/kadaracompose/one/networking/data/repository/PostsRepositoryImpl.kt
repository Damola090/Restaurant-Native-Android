package com.example.kadaracompose.one.networking.data.repository

import com.example.kadaracompose.one.networking.domain.model.NetworkResult
import com.example.kadaracompose.one.networking.data.remote.api.PostsApi
import com.example.kadaracompose.one.networking.data.remote.dto.CreatePostDto
import com.example.kadaracompose.one.networking.domain.model.NewPost
import com.example.kadaracompose.one.networking.domain.model.Post
import java.io.IOException
import javax.inject.Inject

/**
 * This is where HTTP reality meets your clean domain.
 *
 * The repository's job:
 *  1. Call the API
 *  2. Handle every possible outcome (success, HTTP error, network error)
 *  3. Map DTOs → domain models
 *  4. Return NetworkResult so callers never have to deal with exceptions
 *
 * Notice that ViewModels and UseCases never see Retrofit, Response<T>,
 * or any HTTP concept — that all stays here.
 */
class PostsRepositoryImpl @Inject constructor(
    private val api: PostsApi
) : PostsRepository {

    override suspend fun getPosts(): NetworkResult<List<Post>> {
        return safeApiCall {
            val response = api.getPosts()
            if (response.isSuccessful) {
                val posts = response.body()?.map { it.toDomain() } ?: emptyList()
                NetworkResult.Success(posts)
            } else {
                NetworkResult.Error(
                    message = response.errorBody()?.string() ?: "Unknown error",
                    code = response.code()
                )
            }
        }
    }

    override suspend fun getPostById(id: Int): NetworkResult<Post> {
        return safeApiCall {
            val response = api.getPostById(id)
            if (response.isSuccessful) {
                val post = response.body()?.toDomain()
                    ?: return@safeApiCall NetworkResult.Error("Empty response body")
                NetworkResult.Success(post)
            } else {
                NetworkResult.Error(
                    message = parseErrorMessage(response.code()),
                    code = response.code()
                )
            }
        }
    }

    override suspend fun createPost(newPost: NewPost): NetworkResult<Post> {
        return safeApiCall {
            val dto = CreatePostDto(
                title = newPost.title,
                body = newPost.body,
                userId = newPost.userId
            )
            val response = api.createPost(dto)
            if (response.isSuccessful) {
                val created = response.body()?.toDomain()
                    ?: return@safeApiCall NetworkResult.Error("Empty response body")
                NetworkResult.Success(created)
            } else {
                NetworkResult.Error(
                    message = parseErrorMessage(response.code()),
                    code = response.code()
                )
            }
        }
    }

    override suspend fun deletePost(id: Int): NetworkResult<Unit> {
        return safeApiCall {
            val response = api.deletePost(id)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(
                    message = parseErrorMessage(response.code()),
                    code = response.code()
                )
            }
        }
    }

    /**
     * The most important helper in the repository.
     *
     * Wraps every API call in a try-catch so:
     * - IOException → no internet / DNS failure / timeout
     * - Any other exception → unexpected crash
     *
     * Without this, a dropped network connection would crash your app.
     * With this, it becomes a clean NetworkResult.Error the UI can handle.
     */
    private suspend fun <T> safeApiCall(
        call: suspend () -> NetworkResult<T>
    ): NetworkResult<T> {
        return try {
            call()
        } catch (e: IOException) {
            // No internet, timeout, DNS failure
            NetworkResult.Error(
                message = "No internet connection. Please check your network.",
                throwable = e
            )
        } catch (e: Exception) {
            // Unexpected error — parsing failure, etc.
            NetworkResult.Error(
                message = e.message ?: "An unexpected error occurred",
                throwable = e
            )
        }
    }

    private fun parseErrorMessage(code: Int): String = when (code) {
        400 -> "Bad request — check the data you're sending"
        401 -> "Unauthorised — please log in again"
        403 -> "Forbidden — you don't have permission"
        404 -> "Not found"
        408 -> "Request timed out"
        429 -> "Too many requests — please slow down"
        in 500..599 -> "Server error ($code) — try again later"
        else -> "Unexpected error ($code)"
    }
}