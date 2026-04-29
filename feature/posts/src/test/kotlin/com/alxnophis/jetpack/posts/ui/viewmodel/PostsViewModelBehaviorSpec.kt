package com.alxnophis.jetpack.posts.ui.viewmodel

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.posts.data.model.Post
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.mockito.Mockito.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
internal class PostsViewModelBehaviorSpec :
    BehaviorSpec(
        {
            val postsRepositoryMock: PostsRepository = mock()
            val testDispatcher = UnconfinedTestDispatcher()

            beforeSpec {
                Dispatchers.setMain(testDispatcher)
            }

            afterSpec {
                Dispatchers.resetMain()
            }

            afterTest {
                reset(postsRepositoryMock)
            }

            Given("A PostsViewModel") {
                When("requesting posts update and repository returns successful data") {
                    whenever(postsRepositoryMock.getPosts()).thenReturn(postList.right())
                    val viewModel = PostsViewModel(postsRepository = postsRepositoryMock)

                    viewModel.handleEvent(PostsEvent.OnUpdatePostsRequested)

                    Then("uiState should reflect success with loaded posts") {
                        viewModel.uiState.test {
                            awaitItem() shouldBe
                                PostsUiState.initialState.copy(
                                    status = PostsStatus.Success,
                                    posts = postList.toImmutableList(),
                                )
                            expectNoEvents()
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
                        domainError = PostsError.Unexpected,
                        uiError = PostUiError.Unexpected,
                        description = "Unexpected error",
                    ),
                ).forEach { testCase ->
                    When("requesting posts update fails with ${testCase.description}") {
                        whenever(postsRepositoryMock.getPosts()).thenReturn(testCase.domainError.left())
                        val viewModel = PostsViewModel(postsRepository = postsRepositoryMock)

                        viewModel.handleEvent(PostsEvent.OnUpdatePostsRequested)

                        Then("uiState should reflect error with appropriate error type") {
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

                When("dismissing an error message") {
                    val initialStateWithError =
                        PostsUiState.initialState.copy(
                            status = PostsStatus.Error,
                            error = PostUiError.Network,
                        )
                    whenever(postsRepositoryMock.getPosts()).thenReturn(emptyList<Post>().right())
                    val viewModel =
                        PostsViewModel(
                            postsRepository = postsRepositoryMock,
                            initialUiState = initialStateWithError,
                        )

                    viewModel.handleEvent(PostsEvent.DismissErrorRequested)

                    Then("uiState should clear the error and return to success state") {
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
        },
    ) {
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
