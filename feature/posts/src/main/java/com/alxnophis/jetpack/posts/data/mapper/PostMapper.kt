package com.alxnophis.jetpack.posts.data.mapper

import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.alxnophis.jetpack.posts.data.model.Post

internal fun PostApiModel.mapToPost() = Post(
    id = id,
    userId = userId,
    title = title,
    body = body
)
