package com.alxnophis.jetpack.api.jsonplaceholder

import com.alxnophis.jetpack.api.jsonplaceholder.model.ErrorResponse
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET

interface JsonPlaceholderRetrofitService {
    @GET("/posts")
    suspend fun getPosts(): NetworkResponse<List<PostApiModel>, ErrorResponse>
}
