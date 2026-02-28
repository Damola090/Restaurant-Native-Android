package com.example.kadaracompose.one.networking.domain.model

/**
 * Wraps every network response in one of three states.
 *
 * This sealed class flows through the ENTIRE stack:
 *   Repository → UseCase → ViewModel → UI
 *
 * The UI never has to guess whether data exists or why it failed —
 * the type system enforces exhaustive handling via `when`.
 *
 * Compare this to the naive approach of nullable + boolean flags:
 *   var isLoading: Boolean   ← can be true while data is non-null?
 *   var data: List<Post>?    ← null means loading or error?
 *   var error: String?       ← can coexist with data?
 *
 * With NetworkResult, these states are mutually exclusive by design.
 */
sealed class NetworkResult<out T> {

    /** Request is in flight */
    object Loading : NetworkResult<Nothing>()

    /** Request succeeded, data is guaranteed non-null */
    data class Success<T>(val data: T) : NetworkResult<T>()

    /**
     * Request failed.
     * [message] is human-readable.
     * [code] is the HTTP status code if available (null for network errors).
     */
    data class Error(
        val message: String,
        val code: Int? = null,
        val throwable: Throwable? = null
    ) : NetworkResult<Nothing>()
}

/**
 * Extension functions that make consuming NetworkResult clean at the call site.
 *
 * Usage:
 *   result
 *     .onSuccess { posts -> _state.update { it.copy(posts = posts) } }
 *     .onError   { msg   -> _state.update { it.copy(error = msg)   } }
 */
inline fun <T> NetworkResult<T>.onSuccess(action: (T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) action(data)
    return this
}

inline fun <T> NetworkResult<T>.onError(action: (message: String, code: Int?) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) action(message, code)
    return this
}

inline fun <T> NetworkResult<T>.onLoading(action: () -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Loading) action()
    return this
}

/**
 * Transform the success value without unwrapping.
 * Lets you map domain model → UI model while keeping the wrapper.
 */
inline fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Loading -> NetworkResult.Loading
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
        is NetworkResult.Error   -> this
    }
}
