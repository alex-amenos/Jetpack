package com.alxnophis.jetpack.api.jsonplaceholder

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.api.jsonplaceholder.model.JsonPlaceholderApiError
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.haroldadmin.cnradapter.NetworkResponse

class JsonPlaceholderApiImpl(
    private val service: JsonPlaceholderRetrofitService
) : JsonPlaceholderApi {

    override suspend fun getPost(): Either<JsonPlaceholderApiError, List<PostApiModel>> = Either.catch(
        { JsonPlaceholderApiError.Unexpected },
        {
            return when (val posts = service.getPosts()) {
                is NetworkResponse.Success -> posts.body.right()
                is NetworkResponse.ServerError -> JsonPlaceholderApiError.Server(statusCode = posts.code).left()
                is NetworkResponse.NetworkError -> JsonPlaceholderApiError.Network.left()
                is NetworkResponse.UnknownError -> JsonPlaceholderApiError.Unknown(statusCode = posts.code).left()
            }
        }
    )
}
