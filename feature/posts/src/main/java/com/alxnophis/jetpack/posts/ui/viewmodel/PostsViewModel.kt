package com.alxnophis.jetpack.posts.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.optics.copy
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.posts.R
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.domain.model.PostsError
import com.alxnophis.jetpack.posts.domain.usecase.PostsUseCase
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsState
import com.alxnophis.jetpack.posts.ui.contract.errorMessages
import com.alxnophis.jetpack.posts.ui.contract.isLoading
import com.alxnophis.jetpack.posts.ui.contract.posts
import java.util.UUID
import kotlinx.coroutines.launch

internal class PostsViewModel(
    private val postsUseCase: PostsUseCase,
    private val getRandomUUID: () -> Long = { UUID.randomUUID().mostSignificantBits },
    initialState: PostsState = PostsState.initialState
) : BaseViewModel<PostsEvent, PostsState>(initialState) {

    override fun handleEvent(event: PostsEvent) {
        viewModelScope.launch {
            when (event) {
                PostsEvent.Initialized -> updatePosts()
                PostsEvent.GoBackRequested -> throw IllegalStateException("Go back not implemented")
                is PostsEvent.DismissErrorRequested -> dismissError(event.errorId)
            }
        }
    }

    private fun updatePosts() {
        viewModelScope.launch {
            updateUiState {
                copy { PostsState.isLoading set true }
            }
            getPosts()
                .mapLeft { error: PostsError -> error.mapTo() }
                .fold(
                    { errorMessages: List<ErrorMessage> ->
                        updateUiState {
                            copy {
                                PostsState.isLoading set false
                                PostsState.errorMessages set errorMessages
                            }
                        }
                    },
                    { posts: List<Post> ->
                        updateUiState {
                            copy {
                                PostsState.isLoading set false
                                PostsState.posts set posts
                            }
                        }
                    }
                )
        }
    }

    private suspend fun getPosts(): Either<PostsError, List<Post>> = postsUseCase()

    private fun PostsError.mapTo(): List<ErrorMessage> =
        currentState.errorMessages + ErrorMessage(
            id = getRandomUUID(),
            messageId = when (this@mapTo) {
                PostsError.Network -> R.string.posts_error_network
                PostsError.Server -> R.string.posts_error_server
                PostsError.Unknown -> R.string.posts_error_unknown
                PostsError.Unexpected -> R.string.posts_error_unexpected
            }
        )

    private fun dismissError(errorId: Long) {
        val errorMessages = currentState.errorMessages.filterNot { it.id == errorId }
        updateUiState {
            copy { PostsState.errorMessages set errorMessages }
        }
    }
}
