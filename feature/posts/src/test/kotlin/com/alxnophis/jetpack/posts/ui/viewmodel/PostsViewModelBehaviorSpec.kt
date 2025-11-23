package com.alxnophis.jetpack.posts.ui.viewmodel

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.posts.data.model.PostMother
import com.alxnophis.jetpack.posts.data.model.PostsError
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.ui.contract.PostUiError
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsStatus
import com.alxnophis.jetpack.posts.ui.contract.PostsUiState
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.mockito.Mockito.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
internal class PostsViewModelBehaviorSpec : BehaviorSpec() {
    private val postsRepositoryMock: PostsRepository = mock()

    init {
        Given("A PostsViewModel") {
            When("requesting posts update and repository returns successful data") {
                whenever(postsRepositoryMock.getPosts()).thenReturn(postList.right())
                val viewModel = viewModelMother()
                viewModel.handleEvent(PostsEvent.OnUpdatePostsRequested)

                Then("uiState should reflect success with loaded posts") {
                    runTest {
                        runCurrent()

                        viewModel.uiState.test {
                            awaitItem() shouldBe PostsUiState.initialState.copy(status = PostsStatus.Success, posts = postList.toImmutableList())
                            expectNoEvents()
                        }
                    }
                }
            }

            listOf(
                ErrorTestCase(
                    domainError = PostsError.Network,
                    uiError = PostUiError.Network,
                    description = "Network error",
                ),
                ErrorTestCase(
                    domainError = PostsError.Server,
                    uiError = PostUiError.Server,
                    description = "Server error",
                ),
                ErrorTestCase(
                    domainError = PostsError.Unknown,
                    uiError = PostUiError.Unknown,
                    description = "Unknown error",
                ),
                ErrorTestCase(
                    domainError = PostsError.Unexpected,
                    uiError = PostUiError.Unexpected,
                    description = "Unexpected error",
                ),
            ).forEach { testCase ->
                When("requesting posts update fails with ${testCase.description}") {
                    whenever(postsRepositoryMock.getPosts()).thenReturn(testCase.domainError.left())
                    val viewModel = viewModelMother()
                    viewModel.handleEvent(PostsEvent.OnUpdatePostsRequested)

                    Then("uiState should reflect error with appropriate error type") {
                        runTest {
                            runCurrent()

                            viewModel.uiState.test {
                                awaitItem() shouldBe
                                    PostsUiState.initialState.copy(
                                        status = PostsStatus.Error,
                                        error = testCase.uiError,
                                    )
                                expectNoEvents()
                            }
                        }
                    }
                }
            }

            When("dismissing an error message") {
                val initialStateWithError =
                    PostsUiState.initialState.copy(
                        status = PostsStatus.Error,
                        error = PostUiError.Network,
                    )
                val viewModel = viewModelMother(initialState = initialStateWithError)
                viewModel.handleEvent(PostsEvent.DismissErrorRequested)

                Then("uiState should clear the error and return to success state") {
                    runTest {
                        runCurrent()

                        viewModel.uiState.test {
                            awaitItem() shouldBe
                                initialStateWithError.copy(
                                    status = PostsStatus.Success,
                                    error = null,
                                )
                            expectNoEvents()
                        }
                    }
                }
            }
        }
    }

    @After
    fun tearDown() {
        reset(postsRepositoryMock)
    }

    private fun viewModelMother(
        postsRepository: PostsRepository = postsRepositoryMock,
        initialState: PostsUiState = PostsUiState.initialState,
    ) = PostsViewModel(
        postsRepository = postsRepository,
        initialState = initialState,
    )

    private companion object {
        val post1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
        val post2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
        val postList = listOf(post1, post2)

        data class ErrorTestCase(
            val domainError: PostsError,
            val uiError: PostUiError,
            val description: String,
        )
    }
}
