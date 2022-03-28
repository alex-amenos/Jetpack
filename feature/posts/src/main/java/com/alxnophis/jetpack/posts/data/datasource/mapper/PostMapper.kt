package com.alxnophis.jetpack.posts.data.datasource.mapper

import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.alxnophis.jetpack.posts.domain.model.Post

internal fun List<PostApiModel>.mapToPost(): List<Post> =
    this.map { it.mapToPost() }

private fun PostApiModel.mapToPost() = Post(
    id = this.id,
    userId = this.userId,
    title = this.title,
    body = this.body
)
