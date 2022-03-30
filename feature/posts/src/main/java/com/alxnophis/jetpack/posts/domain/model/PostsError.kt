package com.alxnophis.jetpack.posts.domain.model

sealed class PostsError {
    object Network : PostsError()
    object Server : PostsError()
    object Unknown : PostsError()
}
