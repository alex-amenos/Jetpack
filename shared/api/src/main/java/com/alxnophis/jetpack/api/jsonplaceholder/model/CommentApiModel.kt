package com.alxnophis.jetpack.api.jsonplaceholder.model

data class CommentApiModel(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String,
)
