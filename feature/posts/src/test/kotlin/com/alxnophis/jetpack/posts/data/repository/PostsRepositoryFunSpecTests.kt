package com.alxnophis.jetpack.posts.data.repository

import arrow.core.right
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderRetrofitService
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModelMother
import com.alxnophis.jetpack.posts.domain.model.PostMother
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
private class PostsRepositoryBehaviorTests : FunSpec() {
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val apiDataSourceMock: JsonPlaceholderRetrofitService = mock()
    private lateinit var repositoryMock: PostsRepository

    init {
        beforeTest {
            repositoryMock = PostsRepositoryImpl(
                ioDispatcher = testDispatcher,
                apiDataSource = apiDataSourceMock
            )
        }
        test("WHEN get posts from API succeed THEN return a list of posts ") {
            whenever(apiDataSourceMock.getPosts()).thenReturn(POST_API_LIST.right())

            val result = repositoryMock.getPosts()

            result shouldBe POST_LIST.right()
        }
    }

    companion object {
        private val POST_API_1 = PostApiModelMother(id = 1, userId = 1, title = "title1", body = "body1")
        private val POST_API_2 = PostApiModelMother(id = 2, userId = 2, title = "title2", body = "body2")
        private val POST_API_LIST = listOf(POST_API_1, POST_API_2)
        private val POST_1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
        private val POST_2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
        private val POST_LIST = listOf(POST_1, POST_2)
    }
}
