package com.alxnophis.jetpack.posts.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderApi
import com.alxnophis.jetpack.api.jsonplaceholder.model.JsonPlaceholderApiError
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import com.alxnophis.jetpack.posts.data.mapper.mapToPostList
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.domain.model.PostsError
import kotlinx.coroutines.withContext

class PostsRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val apiDataSource: JsonPlaceholderApi
) : PostsRepository {
    override suspend fun getPosts(): Either<PostsError, List<Post>> =
        withContext(dispatcherProvider.io()) {
            apiDataSource
                .getPost()
                .map { posts: List<PostApiModel> -> posts.mapToPostList() }
                .mapLeft { error ->
                    when (error) {
                        JsonPlaceholderApiError.Network -> PostsError.Network
                        JsonPlaceholderApiError.Unexpected -> PostsError.Unexpected
                        is JsonPlaceholderApiError.Server -> PostsError.Server
                        is JsonPlaceholderApiError.Unknown -> PostsError.Unknown
                    }
                }
        }
}
