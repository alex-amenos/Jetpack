package com.alxnophis.jetpack.api.jsonplaceholder

import com.alxnophis.jetpack.api.jsonplaceholder.model.ErrorResponse
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.haroldadmin.cnradapter.NetworkResponse

class JsonPlaceholderApiImpl(
    private val service: JsonPlaceholderRetrofitService
) : JsonPlaceholderApi {

    override suspend fun getPost(): NetworkResponse<List<PostApiModel>, ErrorResponse> = service.getPosts()
}
