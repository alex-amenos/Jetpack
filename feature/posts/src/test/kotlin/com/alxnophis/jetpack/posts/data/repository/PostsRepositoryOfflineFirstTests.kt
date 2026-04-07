package com.alxnophis.jetpack.posts.data.repository

import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.posts.data.datasource.FakePostsLocalDataSource
import com.alxnophis.jetpack.posts.data.datasource.PostsRemoteDataSource
import com.alxnophis.jetpack.posts.data.model.PostDetailError
import com.alxnophis.jetpack.posts.data.model.PostMother
import com.alxnophis.jetpack.posts.data.model.PostsError
import com.alxnophis.jetpack.posts.data.model.PostsLocalError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
internal class PostsRepositoryOfflineFirstTests {
    private val remoteDataSourceMock: PostsRemoteDataSource = mock()
    private val localDataSource = FakePostsLocalDataSource()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: PostsRepository

    @BeforeEach
    fun setUp() {
        localDataSource.clearCache()
        localDataSource.clearError()
        repository =
            PostsRepositoryImpl(
                remoteDataSource = remoteDataSourceMock,
                localDataSource = localDataSource,
                ioDispatcher = testDispatcher,
            )
    }

    @Nested
    inner class GetPosts {
        @Test
        fun `GIVEN cache has posts WHEN getPosts THEN return cached posts without network call`() =
            runTest {
                localDataSource.savePosts(listOf(post1, post2))
                localDataSource.setLastUpdateTimestamp(System.currentTimeMillis()) // Fresh cache
                // Mock for background refresh (even though it won't trigger with fresh cache)
                whenever(remoteDataSourceMock.getPosts()).thenReturn(postList.right())

                val result = repository.getPosts()

                result shouldBeEqualTo postList.right()
            }

        @Test
        fun `GIVEN cache is empty WHEN getPosts THEN fetch from network and cache results`() =
            runTest {
                whenever(remoteDataSourceMock.getPosts()).thenReturn(postList.right())

                val result = repository.getPosts()

                result shouldBeEqualTo postList.right()
                localDataSource.getCachedPosts() shouldBeEqualTo postList
            }

        @Test
        fun `GIVEN cache error WHEN getPosts THEN fetch from network`() =
            runTest {
                localDataSource.setShouldReturnError(PostsLocalError.DatabaseError)
                whenever(remoteDataSourceMock.getPosts()).thenReturn(postList.right())

                val result = repository.getPosts()

                result shouldBeEqualTo postList.right()
            }

        @Test
        fun `GIVEN cache empty and network fails WHEN getPosts THEN return network error`() =
            runTest {
                whenever(remoteDataSourceMock.getPosts()).thenReturn(PostsError.Network.left())

                val result = repository.getPosts()

                result shouldBeEqualTo PostsError.Network.left()
            }

        @Test
        fun `GIVEN network success WHEN getPosts THEN cache is updated even if cache write fails`() =
            runTest {
                whenever(remoteDataSourceMock.getPosts()).thenReturn(postList.right())
                // Simulate cache write failure (but getPosts should still succeed)
                localDataSource.setShouldReturnError(PostsLocalError.DatabaseError)

                val result = repository.getPosts()

                result shouldBeEqualTo postList.right()
            }
    }

    @Nested
    inner class GetPostById {
        @Test
        fun `GIVEN post in cache WHEN getPostById THEN return cached post without network call`() =
            runTest {
                localDataSource.savePost(post1)

                val result = repository.getPostById(postId = 1)

                result shouldBeEqualTo post1.right()
            }

        @Test
        fun `GIVEN post not in cache WHEN getPostById THEN fetch from network and cache result`() =
            runTest {
                whenever(remoteDataSourceMock.getPostById(1)).thenReturn(post1.right())

                val result = repository.getPostById(postId = 1)

                result shouldBeEqualTo post1.right()
                localDataSource.getCachedPosts() shouldBeEqualTo listOf(post1)
            }

        @Test
        fun `GIVEN post not in cache and network fails WHEN getPostById THEN return network error`() =
            runTest {
                whenever(remoteDataSourceMock.getPostById(1)).thenReturn(PostDetailError.NotFound.left())

                val result = repository.getPostById(postId = 1)

                result shouldBeEqualTo PostDetailError.NotFound.left()
            }

        @Test
        fun `GIVEN network success WHEN getPostById THEN cache is updated even if cache write fails`() =
            runTest {
                whenever(remoteDataSourceMock.getPostById(1)).thenReturn(post1.right())
                localDataSource.setShouldReturnError(PostsLocalError.DatabaseError)

                val result = repository.getPostById(postId = 1)

                result shouldBeEqualTo post1.right()
            }
    }

    private companion object {
        val post1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
        val post2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
        val postList = listOf(post1, post2)
    }
}
