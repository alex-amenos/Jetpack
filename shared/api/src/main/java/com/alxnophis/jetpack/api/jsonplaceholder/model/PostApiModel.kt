package com.alxnophis.jetpack.api.jsonplaceholder.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostApiModel(
    @SerialName("id") val id: Long,
    @SerialName("userId") val userId: Long,
    @SerialName("title") val title: String,
    @SerialName("body") val body: String,
)
