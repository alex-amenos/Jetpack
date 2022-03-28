package com.alxnophis.jetpack.api.jsonplaceholder

import com.alxnophis.jetpack.api.jsonplaceholder.model.Post
import retrofit2.Response

class JsonPlaceholderApiImpl(
    private val service: JsonPlaceholderRetrofitService
) : JsonPlaceholderApi {

    override suspend fun getPost(): Response<List<Post>> = service.getPosts()
}
