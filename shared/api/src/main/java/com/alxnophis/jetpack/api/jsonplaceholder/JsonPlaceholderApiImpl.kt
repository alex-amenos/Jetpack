package com.alxnophis.jetpack.api.jsonplaceholder

import com.alxnophis.jetpack.api.jsonplaceholder.model.AlbumApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.CommentApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.ErrorResponse
import com.alxnophis.jetpack.api.jsonplaceholder.model.PhotoApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.UserApiModel
import com.haroldadmin.cnradapter.NetworkResponse

class JsonPlaceholderApiImpl(
    private val service: JsonPlaceholderRetrofitService
) : JsonPlaceholderApi {

    override suspend fun getPost(): NetworkResponse<List<PostApiModel>, ErrorResponse> =
        service.getPosts()

    override suspend fun getPostById(id: Int): NetworkResponse<PostApiModel, ErrorResponse> =
        service.getPostById(id)

    override suspend fun getCommentsByPostId(postId: Int): NetworkResponse<List<CommentApiModel>, ErrorResponse> =
        service.getCommentsByPostId(postId)

    override suspend fun getUsers(): NetworkResponse<List<UserApiModel>, ErrorResponse> =
        service.getUsers()

    override suspend fun getUserById(id: Int): NetworkResponse<UserApiModel, ErrorResponse> =
        service.getUsersBy(id)

    override suspend fun getAlbums(): NetworkResponse<List<AlbumApiModel>, ErrorResponse> =
        service.getAlbums()

    override suspend fun getPhotos(): NetworkResponse<List<PhotoApiModel>, ErrorResponse> =
        service.getPhotos()

    override suspend fun getPhotosByAlbumId(albumId: Int): NetworkResponse<List<PhotoApiModel>, ErrorResponse> =
        service.getPhotosByAlbumId(albumId)
}
