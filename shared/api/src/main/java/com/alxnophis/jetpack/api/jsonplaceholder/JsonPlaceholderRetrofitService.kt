package com.alxnophis.jetpack.api.jsonplaceholder

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.alxnophis.jetpack.api.jsonplaceholder.model.AlbumApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.CommentApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.PhotoApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModel
import com.alxnophis.jetpack.api.jsonplaceholder.model.UserApiModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JsonPlaceholderPostsService {
    @GET("posts")
    suspend fun getPosts(): Either<CallError, List<PostApiModel>>

    @GET("posts/{id}")
    suspend fun getPostById(
        @Path("id") id: Long,
    ): Either<CallError, PostApiModel>
}

fun interface JsonPlaceholderCommentsService {
    @GET("comments")
    suspend fun getCommentsByPostId(
        @Query("postId") postId: Long,
    ): Either<CallError, List<CommentApiModel>>
}

interface JsonPlaceholderUsersService {
    @GET("users")
    suspend fun getUsers(): Either<CallError, List<UserApiModel>>

    @GET("users/{id}")
    suspend fun getUsersBy(
        @Path("id") userId: Long,
    ): Either<CallError, UserApiModel>
}

fun interface JsonPlaceholderAlbumsService {
    @GET("albums")
    suspend fun getAlbums(): Either<CallError, List<AlbumApiModel>>
}

interface JsonPlaceholderPhotosService {
    @GET("photos")
    suspend fun getPhotos(): Either<CallError, List<PhotoApiModel>>

    @GET("photos")
    suspend fun getPhotosByAlbumId(
        @Query("albumId") albumId: Int,
    ): Either<CallError, List<PhotoApiModel>>
}
