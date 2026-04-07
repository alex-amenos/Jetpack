package com.alxnophis.jetpack.posts.data.datasource

import arrow.core.Either
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostDetailLocalError
import com.alxnophis.jetpack.posts.data.model.PostsLocalError

internal interface PostsLocalDataSource {
    suspend fun getPosts(): Either<PostsLocalError, List<Post>>

    suspend fun getPostById(postId: Long): Either<PostDetailLocalError, Post>

    suspend fun savePosts(posts: List<Post>): Either<PostsLocalError, Unit>

    suspend fun savePost(post: Post): Either<PostDetailLocalError, Unit>

    suspend fun clearAllPosts(): Either<PostsLocalError, Unit>
}
