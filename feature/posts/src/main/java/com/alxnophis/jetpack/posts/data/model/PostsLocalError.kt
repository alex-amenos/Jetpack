package com.alxnophis.jetpack.posts.data.model

sealed class PostsLocalError {
    data object DatabaseError : PostsLocalError()

    data object EmptyCache : PostsLocalError()

    data object Unexpected : PostsLocalError()
}
