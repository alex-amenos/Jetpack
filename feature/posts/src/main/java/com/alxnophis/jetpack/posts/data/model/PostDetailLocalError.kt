package com.alxnophis.jetpack.posts.data.model

sealed class PostDetailLocalError {
    data object DatabaseError : PostDetailLocalError()

    data object NotFound : PostDetailLocalError()

    data object Unexpected : PostDetailLocalError()
}
