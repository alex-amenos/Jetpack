package com.alxnophis.jetpack.posts.data.datasource

import arrow.core.Either
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostDetailError
import com.alxnophis.jetpack.posts.data.model.PostsError

internal interface PostsLocalDataSource {
    suspend fun getPosts(): Either<PostsError, List<Post>>

    suspend fun getPostById(postId: Long): Either<PostDetailError, Post>

    suspend fun savePosts(posts: List<Post>): Either<PostsError, Unit>

    suspend fun savePost(post: Post): Either<PostDetailError, Unit>

    suspend fun clearAllPosts(): Either<PostsError, Unit>
}
