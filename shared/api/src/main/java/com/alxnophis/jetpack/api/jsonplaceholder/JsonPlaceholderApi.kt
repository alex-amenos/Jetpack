package com.alxnophis.jetpack.api.jsonplaceholder

import arrow.core.Either
import com.alxnophis.jetpack.api.jsonplaceholder.model.JsonPlaceholderError
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel

interface JsonPlaceholderApi {
    suspend fun getPost(): Either<JsonPlaceholderError, List<PostApiModel>>
}
