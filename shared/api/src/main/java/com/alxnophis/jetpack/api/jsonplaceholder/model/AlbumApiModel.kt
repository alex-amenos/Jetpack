package com.alxnophis.jetpack.api.jsonplaceholder.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlbumApiModel(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("userId") val userId: Int,
)
