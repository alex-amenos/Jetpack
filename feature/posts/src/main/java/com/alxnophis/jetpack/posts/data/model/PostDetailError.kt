package com.alxnophis.jetpack.posts.data.model

sealed class PostDetailError {
    data object Network : PostDetailError()

    data object NotFound : PostDetailError()

    data object Server : PostDetailError()

    data object Unknown : PostDetailError()

    data object Unexpected : PostDetailError()
}
