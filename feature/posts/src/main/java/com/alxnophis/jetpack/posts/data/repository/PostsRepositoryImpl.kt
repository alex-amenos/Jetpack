package com.alxnophis.jetpack.posts.data.repository

import arrow.core.Either
import arrow.core.right
import com.alxnophis.jetpack.posts.data.datasource.PostsLocalDataSource
import com.alxnophis.jetpack.posts.data.datasource.PostsRemoteDataSource
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostDetailError
import com.alxnophis.jetpack.posts.data.model.PostsError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class PostsRepositoryImpl(
    private val remoteDataSource: PostsRemoteDataSource,
    private val localDataSource: PostsLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PostsRepository {
    override suspend fun getPosts(): Either<PostsError, List<Post>> =
        withContext(ioDispatcher) {
            // Try to get from cache first
            localDataSource
                .getPosts()
                .fold(
                    { localError ->
                        Timber.d("Cache miss or error: $localError, fetching from network")
                        // Cache miss or error, fetch from network
                        fetchAndCachePosts()
                    },
                    { cachedPosts ->
                        Timber.d("Cache hit: ${cachedPosts.size} posts")
                        // Return cached data and refresh in background
                        cachedPosts
                            .right()
                            .also {
                                // Optionally trigger background refresh
                                // This is a simple implementation - you could add timestamp logic here
                            }
                    },
                )
        }

    private suspend fun fetchAndCachePosts(): Either<PostsError, List<Post>> =
        remoteDataSource
            .getPosts()
            .onRight { posts ->
                // Save to cache, but don't fail if cache write fails
                localDataSource
                    .savePosts(posts)
                    .onLeft { cacheError ->
                        Timber.e("Failed to cache posts: $cacheError")
                    }
            }

    override suspend fun getPostById(postId: Long): Either<PostDetailError, Post> =
        withContext(ioDispatcher) {
            // Try to get from cache first
            localDataSource
                .getPostById(postId)
                .fold(
                    { localError ->
                        Timber.d("Post $postId not in cache: $localError, fetching from network")
                        // Not in cache, fetch from network
                        fetchAndCachePost(postId)
                    },
                    { cachedPost ->
                        Timber.d("Post $postId found in cache")
                        cachedPost.right()
                    },
                )
        }

    private suspend fun fetchAndCachePost(postId: Long): Either<PostDetailError, Post> =
        remoteDataSource
            .getPostById(postId)
            .onRight { post ->
                // Save to cache, but don't fail if cache write fails
                localDataSource
                    .savePost(post)
                    .onLeft { cacheError ->
                        Timber.e("Failed to cache post $postId: $cacheError")
                    }
            }
}
