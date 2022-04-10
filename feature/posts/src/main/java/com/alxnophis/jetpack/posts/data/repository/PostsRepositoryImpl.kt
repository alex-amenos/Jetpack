package com.alxnophis.jetpack.posts.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.posts.data.datasource.PostDataSource
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.domain.model.PostsError

class PostsRepositoryImpl(
    private val dataSource: PostDataSource
) : PostsRepository {
    override suspend fun getPosts(): Either<PostsError, List<Post>> = dataSource.getPosts()
}
