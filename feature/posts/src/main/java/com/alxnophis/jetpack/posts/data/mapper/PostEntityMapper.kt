package com.alxnophis.jetpack.posts.data.mapper

import com.alxnophis.jetpack.posts.data.database.entity.PostEntity
import com.alxnophis.jetpack.posts.data.model.Post

internal fun List<PostEntity>.mapToPosts(): List<Post> = map { it.mapToPost() }

internal fun PostEntity.mapToPost() =
    Post(
        id = id,
        userId = userId,
        title = title,
        body = body,
    )

internal fun List<Post>.mapToPostEntities(): List<PostEntity> = map { it.mapToPostEntity() }

internal fun Post.mapToPostEntity() =
    PostEntity(
        id = id,
        userId = userId,
        title = title,
        body = body,
    )
