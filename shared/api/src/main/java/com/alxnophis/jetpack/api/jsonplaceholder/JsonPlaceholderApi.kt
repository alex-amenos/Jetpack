package com.alxnophis.jetpack.api.jsonplaceholder

import com.alxnophis.jetpack.api.jsonplaceholder.model.AlbumApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.CommentApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.ErrorResponse
import com.alxnophis.jetpack.api.jsonplaceholder.model.PhotoApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.UserApiModel
import com.haroldadmin.cnradapter.NetworkResponse

interface JsonPlaceholderApi {
    suspend fun getPost(): NetworkResponse<List<PostApiModel>, ErrorResponse>
    suspend fun getPostById(id: Int): NetworkResponse<PostApiModel, ErrorResponse>
    suspend fun getCommentsByPostId(postId: Int): NetworkResponse<List<CommentApiModel>, ErrorResponse>
    suspend fun getUsers(): NetworkResponse<List<UserApiModel>, ErrorResponse>
    suspend fun getUserById(id: Int): NetworkResponse<UserApiModel, ErrorResponse>
    suspend fun getAlbums(): NetworkResponse<List<AlbumApiModel>, ErrorResponse>
    suspend fun getPhotos(): NetworkResponse<List<PhotoApiModel>, ErrorResponse>
    suspend fun getPhotosByAlbumId(albumId: Int): NetworkResponse<List<PhotoApiModel>, ErrorResponse>
}
