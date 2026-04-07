package com.alxnophis.jetpack.posts.data.datasource

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostDetailLocalError
import com.alxnophis.jetpack.posts.data.model.PostsLocalError

internal class FakePostsLocalDataSource : PostsLocalDataSource {
    private val cachedPosts = mutableListOf<Post>()
    private var shouldReturnError = false
    private var errorToReturn: PostsLocalError = PostsLocalError.DatabaseError

    fun setShouldReturnError(error: PostsLocalError) {
        shouldReturnError = true
        errorToReturn = error
    }

    fun clearError() {
        shouldReturnError = false
    }

    fun clearCache() {
        cachedPosts.clear()
    }

    fun getCachedPosts(): List<Post> = cachedPosts.toList()

    override suspend fun getPosts(): Either<PostsLocalError, List<Post>> =
        if (shouldReturnError) {
            errorToReturn.left()
        } else if (cachedPosts.isEmpty()) {
            PostsLocalError.EmptyCache.left()
        } else {
            cachedPosts
                .toList()
                .right()
        }

    override suspend fun getPostById(postId: Long): Either<PostDetailLocalError, Post> =
        if (shouldReturnError) {
            PostDetailLocalError.DatabaseError.left()
        } else {
            cachedPosts
                .find { it.id == postId }
                ?.right() ?: PostDetailLocalError.NotFound.left()
        }

    override suspend fun savePosts(posts: List<Post>): Either<PostsLocalError, Unit> =
        if (shouldReturnError) {
            errorToReturn.left()
        } else {
            cachedPosts.clear()
            cachedPosts.addAll(posts)
            Unit.right()
        }

    override suspend fun savePost(post: Post): Either<PostDetailLocalError, Unit> =
        if (shouldReturnError) {
            PostDetailLocalError.DatabaseError.left()
        } else {
            val index = cachedPosts.indexOfFirst { it.id == post.id }
            if (index != -1) {
                cachedPosts[index] = post
            } else {
                cachedPosts.add(post)
            }
            Unit.right()
        }

    override suspend fun clearAllPosts(): Either<PostsLocalError, Unit> =
        if (shouldReturnError) {
            errorToReturn.left()
        } else {
            cachedPosts.clear()
            Unit.right()
        }
}
