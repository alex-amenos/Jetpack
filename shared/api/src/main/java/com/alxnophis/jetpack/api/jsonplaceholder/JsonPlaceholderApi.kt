package com.alxnophis.jetpack.api.jsonplaceholder

import com.alxnophis.jetpack.api.jsonplaceholder.model.Post
import retrofit2.Response

interface JsonPlaceholderApi {
    suspend fun getPost(): Response<List<Post>>
}
