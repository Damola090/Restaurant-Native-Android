package com.example.kadaracompose.one.networking.data.repository

import com.example.kadaracompose.one.networking.domain.model.NetworkResult
import com.example.kadaracompose.one.networking.domain.model.NewPost
import com.example.kadaracompose.one.networking.domain.model.Post

interface PostsRepository {
    suspend fun getPosts(): NetworkResult<List<Post>>
    suspend fun getPostById(id: Int): NetworkResult<Post>
    suspend fun createPost(newPost: NewPost): NetworkResult<Post>
    suspend fun deletePost(id: Int): NetworkResult<Unit>
}
