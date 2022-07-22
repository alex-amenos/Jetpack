package com.alxnophis.jetpack.api.jsonplaceholder

import arrow.core.Either
import com.alxnophis.jetpack.api.jsonplaceholder.model.JsonPlaceholderApiError
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel

interface JsonPlaceholderApi {
    suspend fun getPost(): Either<JsonPlaceholderApiError, List<PostApiModel>>
}
