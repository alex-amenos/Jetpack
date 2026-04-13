package com.alxnophis.jetpack.posts.data.model

data class Post(
    val id: Long,
    val userId: Long,
    val title: String,
    val body: String,
) {
    val titleCapitalized: String
        get() = title.replaceFirstChar { it.uppercase() }
}
