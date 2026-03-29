package com.alxnophis.jetpack.posts.data.datasource

import arrow.core.Either
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostDetailError
import com.alxnophis.jetpack.posts.data.model.PostsError

internal interface PostsDataSource {
    suspend fun getPosts(): Either<PostsError, List<Post>>

    suspend fun getPostById(postId: Int): Either<PostDetailError, Post>
}
