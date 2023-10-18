package com.alxnophis.jetpack.posts.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostsError

interface PostsRepository {
    suspend fun getPosts(): Either<PostsError, List<Post>>
}
