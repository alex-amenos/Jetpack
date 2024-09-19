package com.alxnophis.jetpack.posts.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.kotlin.constants.VIEW_MODEL_STOP_TIMEOUT_MILLIS
import com.alxnophis.jetpack.posts.R
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostsError
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsState
import com.alxnophis.jetpack.posts.ui.contract.errorMessages
import com.alxnophis.jetpack.posts.ui.contract.isLoading
import com.alxnophis.jetpack.posts.ui.contract.posts
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

internal class PostsViewModel(
    private val postsRepository: PostsRepository,
    initialState: PostsState = PostsState.initialState,
    private val getRandomUUID: () -> Long = { UUID.randomUUID().mostSignificantBits },
) : BaseViewModel<PostsEvent, PostsState>(initialState) {
    override val uiState: StateFlow<PostsState> =
        _uiState
            .onStart { updatePosts() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(VIEW_MODEL_STOP_TIMEOUT_MILLIS),
                initialValue = initialState,
            )

    override fun handleEvent(event: PostsEvent) {
        viewModelScope.launch {
            when (event) {
                PostsEvent.GoBackRequested -> throw IllegalStateException("Go back not implemented in ViewModel")
                PostsEvent.OnUpdatePostRequested -> updatePosts()
                is PostsEvent.OnPostClicked -> throw IllegalStateException("On post clicked not implemented in ViewModel")
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
                    },
                )
        }
    }

    private suspend fun getPosts(): Either<PostsError, List<Post>> = postsRepository.getPosts()

    private fun PostsError.mapTo(): List<ErrorMessage> =
        currentState.errorMessages +
            ErrorMessage(
                id = getRandomUUID(),
                messageId =
                    when (this@mapTo) {
                        PostsError.Network -> R.string.posts_error_network
                        PostsError.Server -> R.string.posts_error_server
                        PostsError.Unknown -> R.string.posts_error_unknown
                        PostsError.Unexpected -> R.string.posts_error_unexpected
                    },
            )

    private fun dismissError(errorId: Long) {
        val errorMessages = currentState.errorMessages.filterNot { it.id == errorId }
        updateUiState {
            copy { PostsState.errorMessages set errorMessages }
        }
    }
}
