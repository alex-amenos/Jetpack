package com.alxnophis.jetpack.posts.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.alxnophis.jetpack.posts.data.database.entity.PostEntity
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldHaveSize
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for PostDao using in-memory Room database.
 * Tests validate actual SQL (@Query/@Insert) behavior using Robolectric.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
internal class PostDaoTests {
    private lateinit var database: PostsDatabase
    private lateinit var postDao: PostDao

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    PostsDatabase::class.java,
                ).allowMainThreadQueries()
                .build()
        postDao = database.postDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `GIVEN posts exist WHEN getAllPosts THEN return all posts`() =
        runTest {
            val posts = listOf(postEntity1, postEntity2)
            postDao.insertPosts(posts)

            val result = postDao.getAllPosts()

            result shouldHaveSize 2
            result shouldContain postEntity1
            result shouldContain postEntity2
        }

    @Test
    fun `GIVEN post exists WHEN getPostById THEN return the post`() =
        runTest {
            postDao.insertPost(postEntity1)

            val result = postDao.getPostById(postId = 1)

            result shouldBeEqualTo postEntity1
        }

    @Test
    fun `GIVEN no post with id WHEN getPostById THEN return null`() =
        runTest {
            val result = postDao.getPostById(postId = 999)

            result shouldBeEqualTo null
        }

    @Test
    fun `GIVEN empty database WHEN getAllPosts THEN return empty list`() =
        runTest {
            val result = postDao.getAllPosts()

            result shouldHaveSize 0
        }

    @Test
    fun `GIVEN posts list WHEN insertPosts THEN posts are stored in database`() =
        runTest {
            val posts = listOf(postEntity1, postEntity2)

            postDao.insertPosts(posts)

            val result = postDao.getAllPosts()
            result shouldHaveSize 2
            result shouldContain postEntity1
            result shouldContain postEntity2
        }

    @Test
    fun `GIVEN single post WHEN insertPost THEN post is stored in database`() =
        runTest {
            postDao.insertPost(postEntity1)

            val result = postDao.getPostById(postId = 1)
            result shouldBeEqualTo postEntity1
        }

    @Test
    fun `GIVEN duplicate post id WHEN insertPost THEN post is replaced`() =
        runTest {
            postDao.insertPost(postEntity1)
            val updatedPost = postEntity1.copy(title = "Updated Title")

            postDao.insertPost(updatedPost)

            val result = postDao.getPostById(postId = 1)
            result shouldBeEqualTo updatedPost
            postDao.getAllPosts() shouldHaveSize 1
        }

    @Test
    fun `GIVEN posts exist WHEN deleteAllPosts THEN all posts are deleted`() =
        runTest {
            postDao.insertPosts(listOf(postEntity1, postEntity2))

            postDao.deleteAllPosts()

            val result = postDao.getAllPosts()
            result shouldHaveSize 0
        }

    @Test
    fun `GIVEN post exists WHEN deletePostById THEN specific post is deleted`() =
        runTest {
            postDao.insertPosts(listOf(postEntity1, postEntity2))

            postDao.deletePostById(postId = 1)

            val result = postDao.getAllPosts()
            result shouldHaveSize 1
            result shouldContain postEntity2
        }

    @Test
    fun `GIVEN post does not exist WHEN deletePostById THEN no error occurs`() =
        runTest {
            postDao.deletePostById(postId = 999)

            val result = postDao.getAllPosts()
            result shouldHaveSize 0
        }

    private companion object {
        val postEntity1 =
            PostEntity(
                id = 1,
                userId = 1,
                title = "Title 1",
                body = "Body 1",
            )
        val postEntity2 =
            PostEntity(
                id = 2,
                userId = 2,
                title = "Title 2",
                body = "Body 2",
            )
    }
}
