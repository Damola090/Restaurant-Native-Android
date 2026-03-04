package com.example.kadaracompose.one.localStorage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kadaracompose.one.networking.presentation.PostsState
import com.example.kadaracompose.one.networking.domain.model.NetworkResult
import com.example.kadaracompose.one.networking.domain.model.NewPost
import com.example.kadaracompose.one.networking.domain.model.onError
import com.example.kadaracompose.one.networking.domain.model.onSuccess
import com.example.kadaracompose.one.networking.domain.usecase.CreatePostUseCase
import com.example.kadaracompose.one.networking.domain.usecase.DeletePostUseCase
import com.example.kadaracompose.one.networking.domain.usecase.GetPostByIdUseCase
import com.example.kadaracompose.one.networking.domain.usecase.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val getPosts: GetPostsUseCase,
    private val getPostById: GetPostByIdUseCase,
    private val createPost: CreatePostUseCase,
    private val deletePost: DeletePostUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PostsState())
    val state = _state.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingPosts = true, postsError = null) }

            when (val result = getPosts()) {
                is NetworkResult.Loading -> Unit // handled above

                is NetworkResult.Success -> _state.update {
                    it.copy(
                        posts = result.data,
                        isLoadingPosts = false
                    )
                }

                is NetworkResult.Error -> _state.update {
                    it.copy(
                        postsError = result.message,
                        isLoadingPosts = false
                    )
                }
            }
        }
    }

    fun loadPostDetail(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingDetail = true, detailError = null) }

            getPostById(id)
                .onSuccess { post ->
                    _state.update { it.copy(selectedPost = post, isLoadingDetail = false) }
                }
                .onError { message, code ->
                    _state.update {
                        it.copy(
                            detailError = "$message (code: $code)",
                            isLoadingDetail = false
                        )
                    }
                }
        }
    }

    fun onCreatePost() {
        val current = _state.value
        if (!current.isFormValid) return

        viewModelScope.launch {
            _state.update { it.copy(isCreating = true, createError = null) }

            createPost(NewPost(title = current.newPostTitle, body = current.newPostBody))
                .onSuccess { created ->
                    _state.update {
                        it.copy(
                            // Optimistically prepend the new post to the list
                            posts = listOf(created) + it.posts,
                            isCreating = false,
                            showCreateForm = false,
                            newPostTitle = "",
                            newPostBody = "",
                            userMessage = "Post created successfully!"
                        )
                    }
                }
                .onError { message, _ ->
                    _state.update {
                        it.copy(isCreating = false, createError = message)
                    }
                }
        }
    }

    fun onDeletePost(id: Int) {
        viewModelScope.launch {
            deletePost(id)
                .onSuccess {
                    _state.update {
                        it.copy(
                            posts = it.posts.filter { post -> post.id != id },
                            userMessage = "Post deleted"
                        )
                    }
                }
                .onError { message, _ ->
                    _state.update { it.copy(userMessage = "Delete failed: $message") }
                }
        }
    }

    // Form input handlers
    fun onTitleChanged(value: String) = _state.update { it.copy(newPostTitle = value) }
    fun onBodyChanged(value: String) = _state.update { it.copy(newPostBody = value) }
    fun onToggleCreateForm() = _state.update { it.copy(showCreateForm = !it.showCreateForm) }
    fun onMessageShown() = _state.update { it.copy(userMessage = null) }
}
