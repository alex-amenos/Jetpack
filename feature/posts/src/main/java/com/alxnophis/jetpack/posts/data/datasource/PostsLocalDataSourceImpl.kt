package com.alxnophis.jetpack.posts.data.datasource

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.posts.data.database.PostDao
import com.alxnophis.jetpack.posts.data.database.PostsMetadataDao
import com.alxnophis.jetpack.posts.data.database.entity.PostsMetadataEntity
import com.alxnophis.jetpack.posts.data.mapper.mapToPost
import com.alxnophis.jetpack.posts.data.mapper.mapToPostEntities
import com.alxnophis.jetpack.posts.data.mapper.mapToPostEntity
import com.alxnophis.jetpack.posts.data.mapper.mapToPosts
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostDetailLocalError
import com.alxnophis.jetpack.posts.data.model.PostsLocalError
import timber.log.Timber

internal class PostsLocalDataSourceImpl(
    private val postDao: PostDao,
    private val postsMetadataDao: PostsMetadataDao,
) : PostsLocalDataSource {
    override suspend fun getPosts(): Either<PostsLocalError, List<Post>> =
        try {
            val posts =
                postDao
                    .getAllPosts()
                    .mapToPosts()
            if (posts.isEmpty()) {
                PostsLocalError.EmptyCache.left()
            } else {
                posts.right()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting posts from local database")
            PostsLocalError.DatabaseError.left()
        }

    override suspend fun getPostById(postId: Long): Either<PostDetailLocalError, Post> =
        try {
            postDao
                .getPostById(postId)
                ?.mapToPost()
                ?.right() ?: PostDetailLocalError.NotFound.left()
        } catch (e: Exception) {
            Timber.e(e, "Error getting post by id from local database")
            PostDetailLocalError.DatabaseError.left()
        }

    override suspend fun savePosts(posts: List<Post>): Either<PostsLocalError, Unit> =
        try {
            postDao.deleteAllPosts()
            postDao.insertPosts(posts.mapToPostEntities())
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error saving posts to local database")
            PostsLocalError.DatabaseError.left()
        }

    override suspend fun savePost(post: Post): Either<PostDetailLocalError, Unit> =
        try {
            postDao.insertPost(post.mapToPostEntity())
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error saving post to local database")
            PostDetailLocalError.DatabaseError.left()
        }

    override suspend fun clearAllPosts(): Either<PostsLocalError, Unit> =
        try {
            postDao.deleteAllPosts()
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error clearing posts from local database")
            PostsLocalError.DatabaseError.left()
        }

    override suspend fun getLastUpdateTimestamp(): Either<PostsLocalError, Long?> =
        try {
            val metadata = postsMetadataDao.getMetadata(PostsMetadataEntity.POSTS_METADATA_ID)
            metadata?.lastUpdateTimestamp.right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting last update timestamp from local database")
            PostsLocalError.DatabaseError.left()
        }

    override suspend fun saveLastUpdateTimestamp(timestamp: Long): Either<PostsLocalError, Unit> =
        try {
            val metadata = PostsMetadataEntity(lastUpdateTimestamp = timestamp)
            postsMetadataDao.insertMetadata(metadata)
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error saving last update timestamp to local database")
            PostsLocalError.DatabaseError.left()
        }
}
