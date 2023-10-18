package com.alxnophis.jetpack.posts.data.model

sealed class PostsError {
    object Network : PostsError()
    object Server : PostsError()
    object Unknown : PostsError()
    object Unexpected : PostsError()
}
