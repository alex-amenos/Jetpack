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
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
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
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExperimentalCoroutinesApi
internal class PostsViewModelUnitTests : BaseViewModelUnitTest() {
    private val postRepositoryMock: PostsRepository = mock()

    override fun beforeEachCompleted() {
        reset(postRepositoryMock)
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

            viewModel.handleEvent(PostsEvent.OnUpdatePostsRequested)

            viewModel.uiState.test {
                skipItems(2)
                awaitItem() shouldBeEqualTo PostsUiState.initialState
                awaitItem() shouldBeEqualTo PostsUiState.initialState.copy(status = PostsStatus.Success, posts = postList.toImmutableList())
                expectNoEvents()
            }
            verify(postRepositoryMock, times(2)).getPosts()
        }
    }

    @Test
    fun `GIVEN a uiState with error WHEN dismiss error is requested THEN update uiState without error`() {
        runTest {
            val initialState = PostsUiState.initialState.copy(error = PostUiError.Network)
            val viewModel = viewModelMother(initialState = initialState)
            whenever(postRepositoryMock.getPosts()).thenReturn(emptyList<Post>().right())

            viewModel.handleEvent(PostsEvent.DismissErrorRequested)

            viewModel.uiState.test {
                skipItems(2)
                awaitItem() shouldBeEqualTo initialState.copy(status = PostsStatus.Success, error = null)
                expectNoEvents()
            }
        }
    }

    private fun viewModelMother(
        postsRepository: PostsRepository = postRepositoryMock,
        initialState: PostsUiState = PostsUiState.initialState,
    ) = PostsViewModel(
        postsRepository = postsRepository,
        initialState = initialState,
    )

    private companion object {
        val post1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
        val post2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
        val postList = listOf(post1, post2)

        @JvmStatic
        private fun postErrorsTestCases() =
            Stream.of(
                Arguments.of(PostsError.Network, PostUiError.Network),
                Arguments.of(PostsError.Server, PostUiError.Server),
                Arguments.of(PostsError.Unknown, PostUiError.Unknown),
                Arguments.of(PostsError.Unexpected, PostUiError.Unexpected),
            )
    }
}
