package com.alxnophis.jetpack.api.jsonplaceholder.model

data class PhotoApiModel(
    val id: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)
