package com.alxnophis.jetpack.posts.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostsError
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.ui.contract.PostUiError
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsStatus
import com.alxnophis.jetpack.posts.ui.contract.PostsUiState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class PostsViewModel(
    private val postsRepository: PostsRepository,
    savedStateHandle: SavedStateHandle,
    initialUiState: PostsUiState = savedStateHandle.get<PostsUiState>(SAVED_STATE_HANDLE_UI_STATE_KEY) ?: PostsUiState.initialState,
) : BaseViewModel<PostsEvent, PostsUiState>(initialUiState) {
    private var hasLoadedInitialData = false

    override val uiState: StateFlow<PostsUiState> =
        _uiState
            .onSubscription {
                if (!hasLoadedInitialData) {
                    hasLoadedInitialData = true
                    updatePosts()
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = initialUiState,
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
        updateAndPersistUiState {
            copy(status = PostsStatus.Loading)
        }
        viewModelScope.launch {
            postsRepository
                .getPosts()
                .fold(
                    { error ->
                        updateAndPersistUiState {
                            copy(
                                status = PostsStatus.Error,
                                error = error.mapToUiError(),
                            )
                        }
                    },
                    { posts: List<Post> ->
                        updateAndPersistUiState {
                            copy(
                                status = PostsStatus.Success,
                                posts = posts.toImmutableList(),
                            )
                        }
                    },
                )
        }
    }

    private fun PostsError.mapToUiError(): PostUiError =
        when (this) {
            PostsError.NoConnectivity -> PostUiError.NoConnectivity
            PostsError.Network -> PostUiError.Network
            PostsError.Server -> PostUiError.Server
            PostsError.Unexpected -> PostUiError.Unexpected
        }

    private fun dismissError() {
        updateAndPersistUiState {
            copy(
                status = PostsStatus.Success,
                error = null,
            )
        }
    }
}
