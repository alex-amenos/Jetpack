package com.alxnophis.jetpack.api.jsonplaceholder

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.api.jsonplaceholder.model.JsonPlaceholderError
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.haroldadmin.cnradapter.NetworkResponse

class JsonPlaceholderApiImpl(
    private val service: JsonPlaceholderRetrofitService
) : JsonPlaceholderApi {

    override suspend fun getPost(): Either<JsonPlaceholderError, List<PostApiModel>> = Either.catch(
        { JsonPlaceholderError.Unexpected },
        {
            return when (val posts = service.getPosts()) {
                is NetworkResponse.Success -> posts.body.right()
                is NetworkResponse.ServerError -> JsonPlaceholderError.Server(statusCode = posts.code).left()
                is NetworkResponse.NetworkError -> JsonPlaceholderError.Network.left()
                is NetworkResponse.UnknownError -> JsonPlaceholderError.Unknown(statusCode = posts.code).left()
            }
        }
    )
}
