package com.alxnophis.jetpack.posts.data.repository

import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderRetrofitService
import com.alxnophis.jetpack.api.jsonplaceholder.model.CallErrorMother
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModelMother
import com.alxnophis.jetpack.posts.data.datasource.FakePostsLocalDataSource
import com.alxnophis.jetpack.posts.data.datasource.PostsRemoteDataSource
import com.alxnophis.jetpack.posts.data.datasource.PostsRemoteDataSourceImp
import com.alxnophis.jetpack.posts.data.model.PostDetailError
import com.alxnophis.jetpack.posts.data.model.PostMother
import com.alxnophis.jetpack.posts.data.model.PostsError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.stream.Stream

@ExperimentalCoroutinesApi
internal class PostsRepositoryIntegrationTests {
    private val apiDataSourceMock: JsonPlaceholderRetrofitService = mock()
    private val postsRemoteDataSourceImp: PostsRemoteDataSource = PostsRemoteDataSourceImp(apiDataSourceMock)
    private val localDataSource = FakePostsLocalDataSource()
    private lateinit var repository: PostsRepository

    @BeforeEach
    fun setUp() {
        localDataSource.clearCache()
        localDataSource.clearError()
        repository =
            PostsRepositoryImpl(
                remoteDataSource = postsRemoteDataSourceImp,
                localDataSource = localDataSource,
            )
    }

    @Nested
    inner class GetPosts {
        @Test
        fun `GIVEN get posts from use case succeed WHEN getPosts THEN the result should be a list of posts`() {
            runTest {
                whenever(apiDataSourceMock.getPosts()).thenReturn(postApiList.right())

                val result = repository.getPosts()

                result shouldBeEqualTo postList.right()
            }
        }

        @ParameterizedTest
        @MethodSource("com.alxnophis.jetpack.posts.data.repository.PostsRepositoryIntegrationTests#provideErrorCases")
        fun `GIVEN get posts from API fails WHEN getPosts THEN the result should be the expected error`(
            apiError: CallError,
            expectedError: PostsError,
        ) = runTest {
            whenever(apiDataSourceMock.getPosts()).thenReturn(apiError.left())

            val result = repository.getPosts()

            result shouldBeEqualTo expectedError.left()
        }
    }

    @Nested
    inner class GetPostsById {
        @Test
        fun `GIVEN get post by id from API succeed WHEN getPostById THEN the result should be a post`() {
            runTest {
                whenever(apiDataSourceMock.getPostById(id = 1)).thenReturn(postApi1.right())

                val result = repository.getPostById(postId = 1)

                result shouldBeEqualTo post1.right()
            }
        }

        @ParameterizedTest
        @MethodSource("com.alxnophis.jetpack.posts.data.repository.PostsRepositoryIntegrationTests#providePostDetailErrorCases")
        fun `GIVEN get post by id from API fails WHEN getPostById THEN the result should be the expected error`(
            apiError: CallError,
            expectedError: PostDetailError,
        ) = runTest {
            whenever(apiDataSourceMock.getPostById(id = 1)).thenReturn(apiError.left())

            val result = repository.getPostById(postId = 1)

            result shouldBeEqualTo expectedError.left()
        }
    }

    private companion object {
        val postApi1 = PostApiModelMother(id = 1, userId = 1, title = "title1", body = "body1")
        val postApi2 = PostApiModelMother(id = 2, userId = 2, title = "title2", body = "body2")
        val postApiList = listOf(postApi1, postApi2)
        val post1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
        val post2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
        val postList = listOf(post1, post2)

        @JvmStatic
        fun provideErrorCases(): Stream<Arguments> =
            Stream.of(
                Arguments.of(CallErrorMother.noConnectivityError(), PostsError.NoConnectivity),
                Arguments.of(CallErrorMother.ioError(), PostsError.Unexpected),
                Arguments.of(CallErrorMother.unexpectedCallError(), PostsError.Unexpected),
                Arguments.of(CallErrorMother.httpError(code = 300), PostsError.Network),
                Arguments.of(CallErrorMother.httpError(code = 400), PostsError.Network),
                Arguments.of(CallErrorMother.httpError(code = 500), PostsError.Server),
            )

        @JvmStatic
        fun providePostDetailErrorCases(): Stream<Arguments> =
            Stream.of(
                Arguments.of(CallErrorMother.noConnectivityError(), PostDetailError.NoConnectivity),
                Arguments.of(CallErrorMother.ioError(), PostDetailError.Unexpected),
                Arguments.of(CallErrorMother.unexpectedCallError(), PostDetailError.Unexpected),
                Arguments.of(CallErrorMother.httpError(code = 300), PostDetailError.Network),
                Arguments.of(CallErrorMother.httpError(code = 400), PostDetailError.Network),
                Arguments.of(CallErrorMother.httpError(code = 500), PostDetailError.Server),
            )
    }
}
