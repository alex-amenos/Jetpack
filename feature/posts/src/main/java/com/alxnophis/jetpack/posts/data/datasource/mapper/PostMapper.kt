package com.alxnophis.jetpack.posts.data.datasource.mapper

import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.alxnophis.jetpack.posts.domain.model.Post

internal fun List<PostApiModel>.mapToPostList(): List<Post> = map {
    Post(
        id = it.id,
        userId = it.userId,
        title = it.title,
        body = it.body
    )
}
