package com.alxnophis.jetpack.posts.data.repository

import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderRetrofitService
import com.alxnophis.jetpack.api.jsonplaceholder.model.CallErrorMother
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModelMother
import com.alxnophis.jetpack.posts.data.datasource.PostsDataSource
import com.alxnophis.jetpack.posts.data.datasource.PostsRemoteDataSource
import com.alxnophis.jetpack.posts.data.model.PostMother
import com.alxnophis.jetpack.posts.data.model.PostsError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
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
    private val postsRemoteDataSource: PostsDataSource = PostsRemoteDataSource(apiDataSourceMock)
    private lateinit var repository: PostsRepository

    @BeforeEach
    fun setUp() {
        repository = PostsRepositoryImpl(postsRemoteDataSource)
    }

    @Test
    fun `GIVEN get posts from use case succeed WHEN getPosts THEN the result should be a list of posts`() {
        runTest {
            whenever(apiDataSourceMock.getPosts()).thenReturn(postApiList.right())

            val result = repository.getPosts()

            result shouldBeEqualTo postList.right()
        }
    }

    @ParameterizedTest
    @MethodSource("provideErrorCases")
    fun `GIVEN get posts from API fails WHEN getPosts THEN the result should be the expected error`(
        apiError: CallError,
        expectedError: PostsError,
    ) = runTest {
        whenever(apiDataSourceMock.getPosts()).thenReturn(apiError.left())

        val result = repository.getPosts()

        result shouldBeEqualTo expectedError.left()
    }

    private companion object {
        val postApi1 = PostApiModelMother(id = 1, userId = 1, title = "title1", body = "body1")
        val postApi2 = PostApiModelMother(id = 2, userId = 2, title = "title2", body = "body2")
        val postApiList = listOf(postApi1, postApi2)
        val post1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
        val post2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
        val postList = listOf(post1, post2)

        @JvmStatic
        fun provideErrorCases(): Stream<Arguments> = Stream.of(
            Arguments.of(CallErrorMother.ioError(), PostsError.Unexpected),
            Arguments.of(CallErrorMother.unexpectedCallError(), PostsError.Unexpected),
            Arguments.of(CallErrorMother.httpError(code = 300), PostsError.Network),
            Arguments.of(CallErrorMother.httpError(code = 400), PostsError.Network),
            Arguments.of(CallErrorMother.httpError(code = 500), PostsError.Server),
        )
    }
}
