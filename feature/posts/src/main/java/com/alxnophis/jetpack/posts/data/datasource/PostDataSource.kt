package com.alxnophis.jetpack.posts.data.datasource

import arrow.core.Either
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.domain.model.PostsError

interface PostDataSource {
    suspend fun getPosts(): Either<PostsError, List<Post>>
}
