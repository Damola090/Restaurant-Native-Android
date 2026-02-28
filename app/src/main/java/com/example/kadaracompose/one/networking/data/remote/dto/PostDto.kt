package com.example.kadaracompose.one.networking.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.example.kadaracompose.one.networking.domain.model.Post

/**
 * DTO = Data Transfer Object.
 * This is the raw shape of what the API actually returns.
 *
 * It exists separately from the domain model for two reasons:
 * 1. API field names often don't match what you want in your app
 *    (e.g. "userId" vs "authorId")
 * 2. APIs can return more data than you need, or nest data differently.
 *    The mapping step lets you reshape it cleanly.
 *
 * @SerializedName maps JSON keys to Kotlin property names.
 */
data class PostDto(
    @SerializedName("id")     val id: Int,
    @SerializedName("title")  val title: String,
    @SerializedName("body")   val body: String,
    @SerializedName("userId") val userId: Int
) {
    /** Maps raw API response → clean domain model */
    fun toDomain(): Post = Post(
        id = id,
        title = title.replaceFirstChar { it.uppercase() }, // clean up the data here
        body = body,
        userId = userId
    )
}

data class CreatePostDto(
    @SerializedName("title")  val title: String,
    @SerializedName("body")   val body: String,
    @SerializedName("userId") val userId: Int
)
