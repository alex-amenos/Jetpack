package com.alxnophis.jetpack.posts.ui.viewmodel

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.posts.data.model.PostMother
import com.alxnophis.jetpack.posts.data.model.PostsError
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsState
import com.alxnophis.jetpack.posts.ui.contract.UiPostError
import com.alxnophis.jetpack.testing.listener.MainCoroutineListener
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class PostsViewModelUnitTests : FunSpec({

    val post1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
    val post2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
    val postList = listOf(post1, post2)
    val postRepositoryMock: PostsRepository = mock()

    register(MainCoroutineListener(StandardTestDispatcher()))

    coroutineTestScope = true

    beforeTest {
        reset(postRepositoryMock)
    }

    test(
        "GIVEN a PostsViewModel with default initial state " +
                "WHEN initialize " +
                "THEN show AND hide loading AND post state should end with a list of posts",
    ) {
        val viewModel = PostsViewModel(postsRepository = postRepositoryMock)
        whenever(postRepositoryMock.getPosts()).thenReturn(postList.right())

        viewModel.uiState.test {
            awaitItem() shouldBe PostsState.initialState
            awaitItem() shouldBe PostsState.initialState.copy(isLoading = false, posts = postList)
            expectNoEvents()
        }
    }

    test(
        "GIVEN a PostsViewModel with default initial state " +
                "WHEN updates posts " +
                "THEN show AND hide loading AND post state should end with a list of posts",
    ) {
        val viewModel = PostsViewModel(postsRepository = postRepositoryMock)
        whenever(postRepositoryMock.getPosts()).thenReturn(postList.right())

        viewModel.uiState.test {
            awaitItem() shouldBe PostsState.initialState
            awaitItem() shouldBe PostsState.initialState.copy(isLoading = false, posts = postList)
            expectNoEvents()
        }
    }

    listOf(
        PostsError.Network to UiPostError.Network,
        PostsError.Server to UiPostError.Server,
        PostsError.Unknown to UiPostError.Unknown,
        PostsError.Unexpected to UiPostError.Unexpected,
    ).forEach { (error, uiError) ->
        test(
            "GIVEN a PostsViewModel with default initial state " +
                    "WHEN updates post fails with ${error::class.simpleName} error" +
                    "THEN show AND hide loading AND post state result should be an UiError",
        ) {
            val viewModel =
                PostsViewModel(postsRepository = postRepositoryMock)
            whenever(postRepositoryMock.getPosts()).thenReturn(error.left())

            viewModel.handleEvent(PostsEvent.OnUpdatePostsRequested)

            viewModel.uiState.test {
                awaitItem() shouldBe PostsState.initialState
                awaitItem() shouldBe
                        PostsState.initialState.copy(
                            isLoading = false,
                            error = uiError,
                        )
                expectNoEvents()
            }
        }
    }

    test(
        "GIVEN a PostViewModel initialized with an error " +
                "WHEN DismissError event " +
                "THEN post state result should be without errors",
    ) {
        val viewModel =
            PostsViewModel(postsRepository = postRepositoryMock)
        whenever(postRepositoryMock.getPosts()).thenReturn(PostsError.Network.left())

        viewModel.handleEvent(PostsEvent.DismissErrorRequested)

        viewModel.uiState.test {
            expectMostRecentItem() shouldBe PostsState.initialState
            expectNoEvents()
        }
    }

    // TODO - Review this test
    test(
        "GIVEN a PostViewModel initialized " +
                "WHEN go back event " +
                "THEN throw IllegalStateException",
    ) {
        val viewModel =
            PostsViewModel(postsRepository = postRepositoryMock)

        shouldThrow<IllegalStateException> {
            viewModel.handleEvent(PostsEvent.GoBackRequested)
        }
    }
})
