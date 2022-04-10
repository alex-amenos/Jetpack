
package com.alxnophis.jetpack.api.jsonplaceholder

import com.alxnophis.jetpack.api.jsonplaceholder.model.AlbumApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.CommentApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.ErrorResponse
import com.alxnophis.jetpack.api.jsonplaceholder.model.PhotoApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.UserApiModel
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path

@Suppress("unused")
interface JsonPlaceholderRetrofitService {

    @GET("/posts")
    suspend fun getPosts(): NetworkResponse<List<PostApiModel>, ErrorResponse>

    @GET("/posts/{id}")
    suspend fun getPostById(
        @Path("id") id: Int
    ): NetworkResponse<PostApiModel, ErrorResponse>

    @GET("/comments?postId={id}")
    suspend fun getCommentsByPostId(
        @Path("id") postId: Int
    ): NetworkResponse<List<CommentApiModel>, ErrorResponse>

    @GET("/users")
    suspend fun getUsers(): NetworkResponse<List<UserApiModel>, ErrorResponse>

    @GET("/users/{id}")
    suspend fun getUsersBy(
        @Path("id") userId: Int
    ): NetworkResponse<UserApiModel, ErrorResponse>

    @GET("/albums")
    suspend fun getAlbums(): NetworkResponse<List<AlbumApiModel>, ErrorResponse>

    @GET("/photos")
    suspend fun getPhotos(): NetworkResponse<List<PhotoApiModel>, ErrorResponse>

    @GET("/photos?album={id}")
    suspend fun getPhotosByAlbumId(
        @Path("id") albumId: Int
    ): NetworkResponse<List<PhotoApiModel>, ErrorResponse>
}
