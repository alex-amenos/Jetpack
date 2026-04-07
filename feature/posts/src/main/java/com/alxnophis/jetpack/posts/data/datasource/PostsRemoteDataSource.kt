package com.alxnophis.jetpack.posts.data.datasource

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.jetpack.api.exception.NoConnectivityException
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderRetrofitService
import com.alxnophis.jetpack.posts.data.mapper.mapToPost
import com.alxnophis.jetpack.posts.data.mapper.mapToPosts
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostDetailError
import com.alxnophis.jetpack.posts.data.model.PostsError
import timber.log.Timber

internal class PostsRemoteDataSource(
    private val apiDataSource: JsonPlaceholderRetrofitService,
) : PostsDataSource {
    override suspend fun getPosts(): Either<PostsError, List<Post>> =
        apiDataSource
            .getPosts()
            .map { posts -> posts.mapToPosts() }
            .mapLeft { error ->
                Timber.e("GET posts error: $error")
                error.mapToError(
                    noConnectivity = PostsError.NoConnectivity,
                    unexpected = PostsError.Unexpected,
                    server = PostsError.Server,
                    network = PostsError.Network,
                )
            }

    override suspend fun getPostById(postId: Long): Either<PostDetailError, Post> =
        apiDataSource
            .getPostById(postId)
            .map { post -> post.mapToPost() }
            .mapLeft { error ->
                Timber.e("GET post by id error: $error")
                error.mapToError(
                    noConnectivity = PostDetailError.NoConnectivity,
                    unexpected = PostDetailError.Unexpected,
                    server = PostDetailError.Server,
                    network = PostDetailError.Network,
                )
            }

    private fun <T> CallError.mapToError(
        noConnectivity: T,
        unexpected: T,
        server: T,
        network: T,
    ): T =
        when (this) {
            is IOError -> if (cause is NoConnectivityException) noConnectivity else unexpected
            is UnexpectedCallError -> unexpected
            is HttpError -> if (code >= 500) server else network
        }
}
