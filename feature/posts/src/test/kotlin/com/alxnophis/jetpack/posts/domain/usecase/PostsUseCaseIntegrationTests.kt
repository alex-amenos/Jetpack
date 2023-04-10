package com.alxnophis.jetpack.posts.domain.usecase

import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderRetrofitService
import com.alxnophis.jetpack.api.jsonplaceholder.model.CallErrorMother
import com.alxnophis.jetpack.api.jsonplaceholder.model.PostApiModelMother
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.data.repository.PostsRepositoryImpl
import com.alxnophis.jetpack.posts.domain.model.PostMother
import com.alxnophis.jetpack.posts.domain.model.PostsError
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
internal class PostsUseCaseIntegrationTests : BehaviorSpec({

    val postApi1 = PostApiModelMother(id = 1, userId = 1, title = "title1", body = "body1")
    val postApi2 = PostApiModelMother(id = 2, userId = 2, title = "title2", body = "body2")
    val postApiList = listOf(postApi1, postApi2)
    val post1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
    val post2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
    val postList = listOf(post1, post2)

    coroutineTestScope = true

    Given("a PostsUseCase") {
        val apiDataSourceMock: JsonPlaceholderRetrofitService = mock()
        val postRepository: PostsRepository = PostsRepositoryImpl(apiDataSource = apiDataSourceMock)
        val useCase = PostsUseCase(repository = postRepository)

        When("Get posts from use case succeed") {
            whenever(apiDataSourceMock.getPosts()).thenReturn(postApiList.right())
            val result = useCase.invoke()

            Then("the result should be a list of posts") {
                result shouldBe postList.right()
            }
        }

        listOf(
            CallErrorMother.ioError() to PostsError.Unexpected,
            CallErrorMother.unexpectedCallError() to PostsError.Unexpected,
            CallErrorMother.httpError(code = 300) to PostsError.Network,
            CallErrorMother.httpError(code = 400) to PostsError.Network,
            CallErrorMother.httpError(code = 500) to PostsError.Server
        ).forEach { (apiError, expectedError) ->
            When("get post from API fails with $apiError") {
                whenever(apiDataSourceMock.getPosts()).thenReturn(apiError.left())
                val result = useCase.invoke()

                Then("the result should be $expectedError") {
                    result shouldBe expectedError.left()
                }
            }
        }
    }
})
