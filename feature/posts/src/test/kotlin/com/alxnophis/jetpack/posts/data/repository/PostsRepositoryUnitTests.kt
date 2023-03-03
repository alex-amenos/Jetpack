package com.alxnophis.jetpack.posts.data.repository

import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderRetrofitService
import com.alxnophis.jetpack.api.jsonplaceholder.model.CallErrorMother
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModelMother
import com.alxnophis.jetpack.posts.domain.model.PostMother
import com.alxnophis.jetpack.posts.domain.model.PostsError
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
private class PostsRepositoryUnitTests : BaseUnitTest() {

    private val apiDataSourceMock: JsonPlaceholderRetrofitService = mock()
    private lateinit var repositoryMock: PostsRepository

    override fun beforeEachCompleted() {
        repositoryMock = PostsRepositoryImpl(
            ioDispatcher = testDispatcher,
            apiDataSource = apiDataSourceMock
        )
    }

    @Test
    fun `WHEN get posts from API succeed THEN return a list of posts`() = runTest {
        whenever(apiDataSourceMock.getPosts()).thenReturn(POST_API_LIST.right())

        val result = repositoryMock.getPosts()

        assertEquals(POST_LIST.right(), result)
    }

    @ParameterizedTest
    @MethodSource("testProvider")
    fun `WHEN get posts from API fails THEN return error `(
        apiCallError: CallError,
        expectedError: PostsError
    ) = runTest {
        whenever(apiDataSourceMock.getPosts()).thenReturn(apiCallError.left())

        val result = repositoryMock.getPosts()

        assertEquals(expectedError.left(), result)
    }

    companion object {
        private val POST_API_1 = PostApiModelMother(id = 1, userId = 1, title = "title1", body = "body1")
        private val POST_API_2 = PostApiModelMother(id = 2, userId = 2, title = "title2", body = "body2")
        private val POST_API_LIST = listOf(POST_API_1, POST_API_2)
        private val POST_1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
        private val POST_2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
        private val POST_LIST = listOf(POST_1, POST_2)

        @JvmStatic
        private fun testProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(
                CallErrorMother.ioError(),
                PostsError.Unexpected
            ),
            Arguments.of(
                CallErrorMother.unexpectedCallError(),
                PostsError.Unexpected
            ),
            Arguments.of(
                CallErrorMother.httpError(code = 300),
                PostsError.Network
            ),
            Arguments.of(
                CallErrorMother.httpError(code = 400),
                PostsError.Network
            ),
            Arguments.of(
                CallErrorMother.httpError(code = 500),
                PostsError.Server
            )
        )
    }
}
