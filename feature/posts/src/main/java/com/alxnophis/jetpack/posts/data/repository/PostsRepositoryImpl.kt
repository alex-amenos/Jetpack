package com.alxnophis.jetpack.posts.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.posts.data.datasource.PostsDataSource
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostDetailError
import com.alxnophis.jetpack.posts.data.model.PostsError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class PostsRepositoryImpl(
    private val remoteDataSource: PostsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PostsRepository {
    override suspend fun getPosts(): Either<PostsError, List<Post>> =
        withContext(ioDispatcher) {
            remoteDataSource.getPosts()
        }

    override suspend fun getPostById(postId: Long): Either<PostDetailError, Post> =
        withContext(ioDispatcher) {
            remoteDataSource.getPostById(postId)
        }
}
