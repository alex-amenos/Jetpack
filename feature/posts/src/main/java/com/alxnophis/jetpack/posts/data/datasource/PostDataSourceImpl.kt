package com.alxnophis.jetpack.posts.data.datasource

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderApi
import com.alxnophis.jetpack.posts.data.datasource.mapper.mapToPost
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.domain.model.PostsError
import com.haroldadmin.cnradapter.NetworkResponse

class PostDataSourceImpl(
    private val api: JsonPlaceholderApi
) : PostDataSource {

    override suspend fun getPosts(): Either<PostsError, List<Post>> = Either.catch(
        { PostsError.Unknown },
        {
            return when (val posts = api.getPost()) {
                is NetworkResponse.Success -> posts.body.mapToPost().right()
                is NetworkResponse.ServerError -> PostsError.Server.left()
                is NetworkResponse.NetworkError -> PostsError.Network.left()
                is NetworkResponse.UnknownError -> PostsError.Unknown.left()
            }
        }
    )
}
