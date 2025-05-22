package com.alxnophis.jetpack.api.jsonplaceholder.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoApiModel(
    @SerialName("id") val id: Int,
    @SerialName("albumId") val albumId: Int,
    @SerialName("title") val title: String,
    @SerialName("url") val url: String,
    @SerialName("thumbnailUrl") val thumbnailUrl: String,
)
