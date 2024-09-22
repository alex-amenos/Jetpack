package com.alxnophis.jetpack.posts.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.kotlin.constants.VIEW_MODEL_STOP_TIMEOUT_MILLIS
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostsError
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsState
import com.alxnophis.jetpack.posts.ui.contract.UiPostError
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class PostsViewModel(
    private val postsRepository: PostsRepository,
    initialState: PostsState = PostsState.initialState,
) : BaseViewModel<PostsEvent, PostsState>(initialState) {
    override val uiState: StateFlow<PostsState> =
        _uiState.onStart { updatePosts() }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(VIEW_MODEL_STOP_TIMEOUT_MILLIS),
            initialValue = initialState,
        )

    override fun handleEvent(event: PostsEvent) {
        viewModelScope.launch {
            when (event) {
                PostsEvent.GoBackRequested -> throw IllegalStateException("Go back not implemented in ViewModel")
                PostsEvent.OnUpdatePostsRequested -> updatePosts()
                is PostsEvent.OnPostClicked -> throw IllegalStateException("On post clicked not implemented in ViewModel")
                is PostsEvent.DismissErrorRequested -> dismissError()
            }
        }
    }

    private fun updatePosts() {
        viewModelScope.launch {
            updateUiState {
                copy(isLoading = true)
            }
            getPosts().fold(
                { error ->
                    updateUiState {
                        copy(
                            isLoading = false,
                            error = error.mapToUiError(),
                        )
                    }
                },
                { posts: List<Post> ->
                    updateUiState {
                        copy(
                            isLoading = false,
                            posts = posts,
                        )
                    }
                },
            )
        }
    }

    private suspend fun getPosts(): Either<PostsError, List<Post>> = postsRepository.getPosts()

    private fun PostsError.mapToUiError(): UiPostError =
        when (this) {
            PostsError.Network -> UiPostError.Network
            PostsError.Server -> UiPostError.Server
            PostsError.Unknown -> UiPostError.Unknown
            PostsError.Unexpected -> UiPostError.Unexpected
        }

    private fun dismissError() {
        updateUiState {
            copy(error = null)
        }
    }
}
