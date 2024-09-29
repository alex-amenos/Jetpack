package com.alxnophis.jetpack.posts.data.repository

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderRetrofitService
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.alxnophis.jetpack.posts.data.mapper.mapToPost
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostsError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PostsRepositoryImpl(
    private val apiDataSource: JsonPlaceholderRetrofitService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PostsRepository {
    override suspend fun getPosts(): Either<PostsError, List<Post>> =
        withContext(ioDispatcher) {
            apiDataSource
                .getPosts()
                .map { posts: List<PostApiModel> ->
                    posts.map { it.mapToPost() }
                }
                .mapLeft { error: CallError ->
                    Timber.d("GET posts error: $error")
                    when {
                        error is IOError -> PostsError.Unexpected
                        error is UnexpectedCallError -> PostsError.Unexpected
                        error is HttpError && error.code >= 500 -> PostsError.Server
                        error is HttpError -> PostsError.Network
                        else -> PostsError.Unknown
                    }
                }
        }
}
