package com.alxnophis.jetpack.posts.data.repository

import arrow.core.right
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderRetrofitService
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModelMother
import com.alxnophis.jetpack.posts.domain.model.PostMother
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
private class PostsRepositoryBehaviorSpecTest : BehaviorSpec(
    {
        val POST_API_1 = PostApiModelMother(id = 1, userId = 1, title = "title1", body = "body1")
        val POST_API_2 = PostApiModelMother(id = 2, userId = 2, title = "title2", body = "body2")
        val POST_API_LIST = listOf(POST_API_1, POST_API_2)
        val POST_1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
        val POST_2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
        val POST_LIST = listOf(POST_1, POST_2)

        val testScheduler = TestCoroutineScheduler()
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        lateinit var apiDataSourceMock: JsonPlaceholderRetrofitService
        lateinit var repositoryMock: PostsRepository
        beforeContainer {
            apiDataSourceMock = mock()
        }
        Given("PostsRepository") {
            testScope.runTest {
                repositoryMock = PostsRepositoryImpl(
                    ioDispatcher = testDispatcher,
                    apiDataSource = apiDataSourceMock
                )
            }
            When("true") {
                Then("true") {
                    true shouldBe true
                }
            }
            When("get posts from API succeed") {
                whenever(apiDataSourceMock.getPosts()).thenReturn(POST_API_LIST.right())
                val result = repositoryMock.getPosts()
                Then("returns a list of posts") {
                    result shouldBe POST_LIST.right()
                }
            }
        }
    }
)
