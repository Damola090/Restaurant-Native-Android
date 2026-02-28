package com.example.kadaracompose.one.networking.data.remote.api

import com.example.kadaracompose.one.networking.data.remote.dto.CreatePostDto
import com.example.kadaracompose.one.networking.data.remote.dto.PostDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit turns this interface into a real HTTP client at runtime.
 *
 * Key decisions here:
 * - Return `Response<T>` instead of `T` directly so we can inspect
 *   HTTP status codes in the repository (not just success/failure).
 * - Keep this interface thin — no business logic, just HTTP description.
 * - Use suspend functions so Retrofit integrates with coroutines natively.
 */
interface PostsApi {

    @GET("posts")
    suspend fun getPosts(): Response<List<PostDto>>

    @GET("posts/{id}")
    suspend fun getPostById(
        @Path("id") id: Int
    ): Response<PostDto>

    @GET("posts")
    suspend fun getPostsByUser(
        @Query("userId") userId: Int    // → /posts?userId=1
    ): Response<List<PostDto>>

    /**
     * POST with a JSON body.
     * @Body tells Retrofit to serialize the object to JSON automatically.
     */
    @POST("posts")
    suspend fun createPost(
        @Body post: CreatePostDto
    ): Response<PostDto>

    /**
     * PUT replaces the entire resource.
     * PATCH would update only the provided fields.
     */
    @PUT("posts/{id}")
    suspend fun updatePost(
        @Path("id") id: Int,
        @Body post: CreatePostDto
    ): Response<PostDto>

    @DELETE("posts/{id}")
    suspend fun deletePost(
        @Path("id") id: Int
    ): Response<Unit>

    /**
     * Example of a public endpoint that skips auth.
     * The AuthInterceptor checks for this header and removes it
     * before the request goes out.
     */
    @GET("posts/1")
    @Headers("No-Auth: true")
    suspend fun getPublicPost(): Response<PostDto>
}