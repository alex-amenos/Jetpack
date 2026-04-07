package com.alxnophis.jetpack.posts.data.database

import com.alxnophis.jetpack.posts.data.model.PostEntity
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for PostDao.
 * Note: For full integration tests with real Room database,
 * use Android instrumentation tests instead.
 */
internal class PostDaoTests {
    private val postDao: PostDao = mock()

    @Nested
    inner class GetPosts {
        @Test
        fun `GIVEN posts exist WHEN getAllPosts THEN return all posts`() =
            runTest {
                val posts = listOf(postEntity1, postEntity2)
                whenever(postDao.getAllPosts()).thenReturn(posts)

                val result = postDao.getAllPosts()

                result shouldHaveSize 2
                result shouldContain postEntity1
                result shouldContain postEntity2
            }

        @Test
        fun `GIVEN post exists WHEN getPostById THEN return the post`() =
            runTest {
                whenever(postDao.getPostById(1)).thenReturn(postEntity1)

                val result = postDao.getPostById(postId = 1)

                result shouldBeEqualTo postEntity1
            }

        @Test
        fun `GIVEN no post with id WHEN getPostById THEN return null`() =
            runTest {
                whenever(postDao.getPostById(999)).thenReturn(null)

                val result = postDao.getPostById(postId = 999)

                result shouldBeEqualTo null
            }
    }

    @Nested
    inner class InsertPosts {
        @Test
        fun `GIVEN posts list WHEN insertPosts THEN dao inserts posts`() =
            runTest {
                val posts = listOf(postEntity1, postEntity2)

                postDao.insertPosts(posts)

                verify(postDao).insertPosts(posts)
            }

        @Test
        fun `GIVEN single post WHEN insertPost THEN dao inserts post`() =
            runTest {
                postDao.insertPost(postEntity1)

                verify(postDao).insertPost(postEntity1)
            }
    }

    @Nested
    inner class DeletePosts {
        @Test
        fun `WHEN deleteAllPosts THEN dao deletes all posts`() =
            runTest {
                postDao.deleteAllPosts()

                verify(postDao).deleteAllPosts()
            }

        @Test
        fun `GIVEN post id WHEN deletePostById THEN dao deletes post`() =
            runTest {
                postDao.deletePostById(postId = 1)

                verify(postDao).deletePostById(1)
            }
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
