package com.alxnophis.jetpack.api.jsonplaceholder

import com.alxnophis.jetpack.api.jsonplaceholder.model.Post
import retrofit2.Response
import retrofit2.http.GET

interface JsonPlaceholderRetrofitService {
    @GET("/posts")
    suspend fun getPosts(): Response<List<Post>>
}
