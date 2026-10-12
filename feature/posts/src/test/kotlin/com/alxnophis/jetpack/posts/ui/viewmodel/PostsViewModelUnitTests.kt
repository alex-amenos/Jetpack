package com.alxnophis.jetpack.posts.ui.viewmodel

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import java.util.stream.Stream
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.alxnophis.jetpack.core.base.provider.BaseRandomProvider
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostMother
import com.alxnophis.jetpack.posts.data.model.PostsError
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.ui.contract.PostUiError
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsStatus
import com.alxnophis.jetpack.posts.ui.contract.PostsUiState
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExperimentalCoroutinesApi
internal class PostsViewModelUnitTests : BaseViewModelUnitTest() {
    private val postRepositoryMock: PostsRepository = mock()
    private val randomProviderMock: BaseRandomProvider = mock()

    override fun beforeEachCompleted() {
        reset(postRepositoryMock)
        reset(randomProviderMock)
        whenever(randomProviderMock.mostSignificantBitsRandomUUID()).thenReturn(TEST_ERROR_ID)
    }

    @Test
    fun `GIVEN get posts succeeds WHEN initialize THEN verify get posts AND update uiState`() {
        runTest {
            val viewModel = viewModelMother()
            whenever(postRepositoryMock.getPosts()).thenReturn(postList.right())

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo PostsUiState.initialState
                awaitItem() shouldBeEqualTo PostsUiState.initialState.copy(status = PostsStatus.Success, posts = postList.toImmutableList())
                expectNoEvents()
            }
            verify(postRepositoryMock).getPosts()
        }
    }

    @ParameterizedTest
    @MethodSource("postErrorsTestCases")
    fun `GIVEN get posts fails WHEN initialize THEN verify get posts AND update uiState with the error`(
        error: PostsError,
        uiError: PostUiError,
    ) {
        runTest {
            val viewModel = viewModelMother()
            whenever(postRepositoryMock.getPosts()).thenReturn(error.left())

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo PostsUiState.initialState
                awaitItem() shouldBeEqualTo PostsUiState.initialState.copy(status = PostsStatus.Error, error = uiError)
                expectNoEvents()
            }
            verify(postRepositoryMock).getPosts()
        }
    }

    @Test
    fun `GIVEN get posts succeeds WHEN update posts is requested THEN verify get posts AND update uiState`() {
        runTest {
            val viewModel = viewModelMother()
            whenever(postRepositoryMock.getPosts())
                .thenReturn(emptyList<Post>().right())
                .thenReturn(postList.right())

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo PostsUiState.initialState
                awaitItem() shouldBeEqualTo PostsUiState.initialState.copy(status = PostsStatus.Success, posts = emptyList<Post>().toImmutableList())

                viewModel.handleEvent(PostsEvent.OnUpdatePostsRequested)

                awaitItem() shouldBeEqualTo PostsUiState.initialState.copy(status = PostsStatus.Loading, posts = emptyList<Post>().toImmutableList())
                awaitItem() shouldBeEqualTo PostsUiState.initialState.copy(status = PostsStatus.Success, posts = postList.toImmutableList())
                expectNoEvents()
            }
            verify(postRepositoryMock, times(2)).getPosts()
        }
    }

    @Test
    fun `GIVEN a uiState with error WHEN dismiss error is requested with matching errorId THEN update uiState without error`() {
        runTest {
            val testError = PostUiError.Network(id = TEST_ERROR_ID)
            val initialState = PostsUiState.initialState.copy(error = testError)
            val viewModel = viewModelMother(initialState = initialState)
            whenever(postRepositoryMock.getPosts()).thenReturn(emptyList<Post>().right())

            viewModel.handleEvent(PostsEvent.DismissErrorRequested(TEST_ERROR_ID))

            viewModel.uiState.test {
                skipItems(2)
                awaitItem() shouldBeEqualTo initialState.copy(status = PostsStatus.Success, error = null)
                expectNoEvents()
            }
        }
    }

    @Test
    fun `GIVEN a uiState with error WHEN dismiss error is requested with different errorId THEN error is not dismissed`() {
        runTest {
            val testError = PostUiError.Network(id = TEST_ERROR_ID)
            val initialState = PostsUiState.initialState.copy(error = testError)
            val viewModel = viewModelMother(initialState = initialState)
            whenever(postRepositoryMock.getPosts()).thenReturn(emptyList<Post>().right())

            viewModel.handleEvent(PostsEvent.DismissErrorRequested(errorId = 999L))

            viewModel.uiState.test {
                skipItems(2)
                expectNoEvents()
            }
        }
    }

    private fun viewModelMother(
        postsRepository: PostsRepository = postRepositoryMock,
        randomProvider: BaseRandomProvider = randomProviderMock,
        initialState: PostsUiState = PostsUiState.initialState,
    ) = PostsViewModel(
        postsRepository = postsRepository,
        randomProvider = randomProvider,
        initialUiState = initialState,
    )

    private companion object {
        const val TEST_ERROR_ID = 100L
        val post1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
        val post2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
        val postList = listOf(post1, post2)

        @JvmStatic
        private fun postErrorsTestCases() =
            Stream.of(
                Arguments.of(PostsError.Network, PostUiError.Network(TEST_ERROR_ID)),
                Arguments.of(PostsError.Server, PostUiError.Server(TEST_ERROR_ID)),
                Arguments.of(PostsError.Unexpected, PostUiError.Unexpected(TEST_ERROR_ID)),
            )
    }
}
