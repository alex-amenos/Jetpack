package com.alxnophis.jetpack.posts.data.datasource

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderRetrofitService
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.alxnophis.jetpack.posts.data.mapper.mapToPost
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostDetailError
import com.alxnophis.jetpack.posts.data.model.PostsError
import timber.log.Timber

internal class PostsRemoteDataSource(
    private val apiDataSource: JsonPlaceholderRetrofitService,
) : PostsDataSource {
    override suspend fun getPosts(): Either<PostsError, List<Post>> = apiDataSource
        .getPosts()
        .map { posts: List<PostApiModel> ->
            posts.map { it.mapToPost() }
        }
        .mapLeft { error: CallError ->
            Timber.e("GET posts error: $error")
            when (error) {
                is IOError -> PostsError.Unexpected
                is UnexpectedCallError -> PostsError.Unexpected
                is HttpError if error.code >= 500 -> PostsError.Server
                is HttpError -> PostsError.Network
                else -> PostsError.Unknown
            }
        }

    override suspend fun getPostById(postId: Int): Either<PostDetailError, Post> = apiDataSource
        .getPostById(postId)
        .map { post: PostApiModel ->
            post.mapToPost()
        }
        .mapLeft { error: CallError ->
            Timber.e("GET post by id error: $error")
            when (error) {
                is IOError -> PostDetailError.Unexpected
                is UnexpectedCallError -> PostDetailError.Unexpected
                is HttpError if error.code >= 500 -> PostDetailError.Server
                is HttpError -> PostDetailError.Network
                else -> PostDetailError.Unknown
            }
        }
}
