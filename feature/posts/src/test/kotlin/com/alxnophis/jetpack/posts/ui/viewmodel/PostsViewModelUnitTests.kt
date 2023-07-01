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
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class PostsViewModelUnitTests : FunSpec({

    val post1 = PostMother(id = 1, userId = 1, title = "title1", body = "body1")
    val post2 = PostMother(id = 2, userId = 2, title = "title2", body = "body2")
    val postList = listOf(post1, post2)

    register(MainCoroutineListener(StandardTestDispatcher()))

    coroutineTestScope = true

    test(
        "GIVEN a PostsViewModel with default initial state " +
            "WHEN initialize " +
            "THEN show AND hide loading AND post state should end with a list of posts"
    ) {
        val postUseCaseMock: PostsUseCase = mock()
        val viewModel = PostsViewModel(
            postsUseCase = postUseCaseMock
        )

        whenever(postUseCaseMock()).thenReturn(postList.right())

        viewModel.uiState.test {
            awaitItem() shouldBe PostsState.initialState
            awaitItem() shouldBe PostsState.initialState.copy(isLoading = true)
            awaitItem() shouldBe PostsState.initialState.copy(isLoading = false, posts = postList)
            expectNoEvents()
        }
    }

    listOf(
        PostsError.Network to R.string.posts_error_network,
        PostsError.Server to R.string.posts_error_server,
        PostsError.Unknown to R.string.posts_error_unknown,
        PostsError.Unexpected to R.string.posts_error_unexpected
    ).forEach { (error, errorMessage) ->
        test(
            "GIVEN a PostsViewModel with default initial state " +
                "WHEN get post fails with ${error::class.simpleName} error" +
                "THEN show AND hide loading AND post state result should be an error with messageId $errorMessage"
        ) {
            val errorId = 1L
            val postUseCaseMock: PostsUseCase = mock()
            val viewModel = PostsViewModel(
                initialState = PostsState.initialState,
                postsUseCase = postUseCaseMock,
                getRandomUUID = { errorId }
            )

            whenever(postUseCaseMock()).thenReturn(error.left())

            viewModel.uiState.test {
                awaitItem() shouldBe PostsState.initialState
                awaitItem() shouldBe PostsState.initialState.copy(isLoading = true)
                awaitItem() shouldBe PostsState.initialState.copy(
                    isLoading = false,
                    errorMessages = listOf(ErrorMessage(id = errorId, messageId = errorMessage))
                )
                expectNoEvents()
            }
        }
    }

    test(
        "GIVEN a PostViewModel initialized with an error " +
            "WHEN DismissError event " +
            "THEN post state result should be without errors"
    ) {
        val postUseCaseMock: PostsUseCase = mock()
        val errorId = 1L
        val viewModel = PostsViewModel(
            initialState = PostsState.initialState,
            getRandomUUID = { errorId },
            postsUseCase = postUseCaseMock
        )
        whenever(postUseCaseMock()).thenReturn(PostsError.Network.left())

        viewModel.handleEvent(PostsEvent.DismissErrorRequested(errorId))

        viewModel.uiState.test {
            expectMostRecentItem() shouldBe PostsState.initialState
            expectNoEvents()
        }
    }

    // TODO - Review this test
    xtest(
        "GIVEN a PostViewModel initialized " +
            "WHEN go back event " +
            "THEN throw IllegalStateException"
    ) {
        val postUseCaseMock: PostsUseCase = mock()
        val errorId = 1L
        val viewModel = PostsViewModel(
            initialState = PostsState.initialState,
            getRandomUUID = { errorId },
            postsUseCase = postUseCaseMock
        )

        shouldThrow<IllegalStateException> {
            viewModel.handleEvent(PostsEvent.GoBackRequested)
        }
    }
})
