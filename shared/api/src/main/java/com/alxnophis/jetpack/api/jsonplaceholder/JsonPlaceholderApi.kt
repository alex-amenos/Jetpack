package com.alxnophis.jetpack.api.jsonplaceholder

import com.alxnophis.jetpack.api.jsonplaceholder.model.ErrorResponse
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.haroldadmin.cnradapter.NetworkResponse

interface JsonPlaceholderApi {
    suspend fun getPost(): NetworkResponse<List<PostApiModel>, ErrorResponse>
}
