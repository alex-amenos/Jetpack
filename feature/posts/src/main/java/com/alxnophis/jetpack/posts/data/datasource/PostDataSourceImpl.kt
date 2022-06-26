package com.alxnophis.jetpack.posts.data.datasource

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderApi
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import com.alxnophis.jetpack.posts.data.datasource.mapper.mapToPostList
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.domain.model.PostsError
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.withContext

class PostDataSourceImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val api: JsonPlaceholderApi
) : PostDataSource {

    override suspend fun getPosts(): Either<PostsError, List<Post>> =
        withContext(dispatcherProvider.io()) {
            Either.catch(
                { PostsError.Unexpected },
                {
                    return@withContext when (val posts = api.getPost()) {
                        is NetworkResponse.Success -> posts.body.mapToPostList().right()
                        is NetworkResponse.ServerError -> PostsError.Server.left()
                        is NetworkResponse.NetworkError -> PostsError.Network.left()
                        is NetworkResponse.UnknownError -> PostsError.Unknown.left()
                    }
                }
            )
        }
}
