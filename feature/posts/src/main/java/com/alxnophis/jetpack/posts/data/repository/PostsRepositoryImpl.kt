package com.alxnophis.jetpack.posts.data.repository

import arrow.core.Either
import arrow.core.right
import com.alxnophis.jetpack.posts.data.datasource.PostsLocalDataSource
import com.alxnophis.jetpack.posts.data.datasource.PostsRemoteDataSource
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostDetailError
import com.alxnophis.jetpack.posts.data.model.PostsError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Implementation of PostsRepository.
 * @param backgroundRefreshScope Scope for background refresh operations.
 *        Should be an application-level scope to avoid leaking coroutines.
 */
internal class PostsRepositoryImpl(
    private val remoteDataSource: PostsRemoteDataSource,
    private val localDataSource: PostsLocalDataSource,
    private val backgroundRefreshScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PostsRepository {
    private val refreshMutex = Mutex()

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
                        // Check if refresh is needed (24 hours = 86400000 ms)
                        triggerBackgroundRefreshIfNeeded()
                        // Return cached data immediately
                        cachedPosts.right()
                    },
                )
        }

    private fun triggerBackgroundRefreshIfNeeded() {
        // Use tryLock to avoid queueing multiple refresh checks
        // If a refresh is already in progress, skip this request
        if (!refreshMutex.tryLock()) {
            Timber.d("Background refresh already in progress, skipping")
            return
        }

        backgroundRefreshScope.launch {
            try {
                localDataSource
                    .getLastUpdateTimestamp()
                    .fold(
                        { error ->
                            Timber.e("Error getting last update timestamp: $error")
                        },
                        { lastUpdateTimestamp: Long? ->
                            val currentTime = System.currentTimeMillis()
                            if (lastUpdateTimestamp == null) {
                                Timber.d("No timestamp found, triggering background refresh")
                                refreshPostsInBackground()
                            } else {
                                val timeSinceLastUpdate = currentTime - lastUpdateTimestamp
                                val shouldRefresh = timeSinceLastUpdate >= CACHE_EXPIRATION_MS

                                if (shouldRefresh) {
                                    Timber.d("Cache expired, triggering background refresh")
                                    refreshPostsInBackground()
                                } else {
                                    Timber.d(
                                        "Cache still fresh, time since last update: ${timeSinceLastUpdate}ms",
                                    )
                                }
                            }
                        },
                    )
            } finally {
                refreshMutex.unlock()
            }
        }
    }

    private suspend fun refreshPostsInBackground() {
        remoteDataSource
            .getPosts()
            .fold(
                { error ->
                    Timber.e("Background refresh failed: $error")
                },
                { posts ->
                    Timber.d("Background refresh successful, updating cache")
                    localDataSource.savePosts(posts)
                    localDataSource.saveLastUpdateTimestamp(System.currentTimeMillis())
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
                // Save timestamp
                localDataSource
                    .saveLastUpdateTimestamp(System.currentTimeMillis())
                    .onLeft { timestampError ->
                        Timber.e("Failed to save timestamp: $timestampError")
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

    private companion object {
        const val CACHE_EXPIRATION_MS = 24 * 60 * 60 * 1000L // 24 hours in milliseconds
    }
}
