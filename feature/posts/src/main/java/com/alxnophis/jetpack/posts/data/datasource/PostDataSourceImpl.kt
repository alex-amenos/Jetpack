package com.alxnophis.jetpack.posts.data.datasource

import arrow.core.Either
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderApi
import com.alxnophis.jetpack.api.jsonplaceholder.model.JsonPlaceholderApiError
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import com.alxnophis.jetpack.posts.domain.model.PostsError
import kotlinx.coroutines.withContext

class PostDataSourceImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val api: JsonPlaceholderApi
) : PostDataSource {

    override suspend fun getPosts(): Either<PostsError, List<PostApiModel>> =
        withContext(dispatcherProvider.io()) {
            api
                .getPost()
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
