package com.alxnophis.jetpack.posts.data.model

sealed class PostsError {
    data object Network : PostsError()
    data object Server : PostsError()
    data object Unknown : PostsError()
    data object Unexpected : PostsError()
}
