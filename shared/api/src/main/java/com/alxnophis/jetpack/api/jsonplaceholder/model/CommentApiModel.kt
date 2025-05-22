package com.alxnophis.jetpack.api.jsonplaceholder.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentApiModel(
    @SerialName("postId") val postId: Int,
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("body") val body: String,
)
