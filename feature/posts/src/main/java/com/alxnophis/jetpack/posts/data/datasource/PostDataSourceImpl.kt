package com.alxnophis.jetpack.posts.data.datasource

import arrow.core.Either
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderApi
import com.alxnophis.jetpack.api.jsonplaceholder.model.JsonPlaceholderError
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import com.alxnophis.jetpack.posts.data.datasource.mapper.mapToPostList
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.domain.model.PostsError
import kotlinx.coroutines.withContext

class PostDataSourceImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val api: JsonPlaceholderApi
) : PostDataSource {

    override suspend fun getPosts(): Either<PostsError, List<Post>> =
        withContext(dispatcherProvider.io()) {
            api
                .getPost()
                .map { posts: List<PostApiModel> -> posts.mapToPostList() }
                .mapLeft { error ->
                    when (error) {
                        JsonPlaceholderError.Network -> PostsError.Network
                        JsonPlaceholderError.Unexpected -> PostsError.Unexpected
                        is JsonPlaceholderError.Server -> PostsError.Server
                        is JsonPlaceholderError.Unknown -> PostsError.Unknown
                    }
                }
        }
}
