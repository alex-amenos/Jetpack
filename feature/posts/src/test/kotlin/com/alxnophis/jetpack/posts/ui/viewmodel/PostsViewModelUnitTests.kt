package com.alxnophis.jetpack.posts.ui.viewmodel

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.posts.R
import com.alxnophis.jetpack.posts.domain.model.PostMother
import com.alxnophis.jetpack.posts.domain.model.PostsError
import com.alxnophis.jetpack.posts.domain.usecase.PostsUseCase
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsState
import com.alxnophis.jetpack.testing.listener.MainCoroutineListener
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class PostsViewModelUnitTests : BehaviorSpec({

    val post1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
    val post2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
    val postList = listOf(post1, post2)

    register(MainCoroutineListener(StandardTestDispatcher()))

    coroutineTestScope = true

    Given("a PostsViewModel") {
        val postUseCaseMock: PostsUseCase = mock()
        val errorId = 1L

        When("init view model succeed") {
            val viewModel = PostsViewModel(postsUseCase = postUseCaseMock)
            whenever(postUseCaseMock.invoke()).thenReturn(postList.right())

            Then("result should be a list of posts") {
                viewModel.uiState.test {
                    awaitItem() shouldBe PostsState()
                    awaitItem() shouldBe PostsState(isLoading = true)
                    awaitItem() shouldBe PostsState(isLoading = false, posts = postList)
                    expectNoEvents()
                }
            }
        }

        listOf(
            PostsError.Network to R.string.posts_error_network,
            PostsError.Server to R.string.posts_error_server,
            PostsError.Unknown to R.string.posts_error_unknown,
            PostsError.Unexpected to R.string.posts_error_unexpected
        ).forEach { (error, errorMessage) ->
            When("init view model fails getting post with $error") {
                val viewModel = PostsViewModel(
                    initialState = PostsState(),
                    getRandomUUID = { errorId },
                    postsUseCase = postUseCaseMock
                )
                whenever(postUseCaseMock.invoke()).thenReturn(error.left())

                Then("result should be an error with messageId $errorMessage") {
                    viewModel.uiState.test {
                        awaitItem() shouldBe PostsState()
                        awaitItem() shouldBe PostsState(isLoading = true)
                        awaitItem() shouldBe PostsState(
                            isLoading = false,
                            errorMessages = listOf(ErrorMessage(id = errorId, messageId = errorMessage))
                        )
                        expectNoEvents()
                    }
                }
            }
        }

        xWhen("dismiss error from view model") {
            whenever(postUseCaseMock.invoke()).thenReturn(PostsError.Network.left())
            val viewModel = PostsViewModel(
                initialState = PostsState(),
                getRandomUUID = { errorId },
                postsUseCase = postUseCaseMock
            )
            viewModel.handleEvent(PostsEvent.DismissError(errorId))

            Then("result should be a state without errors") {
                viewModel.uiState.test {
                    awaitItem() shouldBe PostsState()
                    awaitItem() shouldBe PostsState(isLoading = true)
                    awaitItem() shouldBe PostsState(
                        errorMessages = listOf(ErrorMessage(id = errorId, messageId = R.string.posts_error_network))
                    )
                    awaitItem() shouldBe PostsState()
                    expectNoEvents()
                }
            }
        }
    }
})
