package com.alxnophis.jetpack.posts.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.alxnophis.jetpack.core.base.provider.BaseRandomProvider
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.data.model.PostsError
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.ui.contract.PostUiError
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsStatus
import com.alxnophis.jetpack.posts.ui.contract.PostsUiState
import com.alxnophis.jetpack.posts.ui.contract.error
import com.alxnophis.jetpack.posts.ui.contract.posts
import com.alxnophis.jetpack.posts.ui.contract.status

internal class PostsViewModel(
    private val postsRepository: PostsRepository,
    private val randomProvider: BaseRandomProvider = BaseRandomProvider(),
    initialUiState: PostsUiState = PostsUiState.initialState,
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
                PostsEvent.OnUpdatePostsRequested -> updatePosts()
                is PostsEvent.DismissErrorRequested -> dismissError(event.errorId)
            }
        }
    }

    private fun updatePosts() {
        updateUiState {
            copy {
                PostsUiState.status set PostsStatus.Loading
            }
        }
        viewModelScope.launch {
            postsRepository
                .getPosts()
                .fold(
                    { error ->
                        updateUiState {
                            copy {
                                PostsUiState.status set PostsStatus.Error
                                PostsUiState.error set error.mapToUiError()
                            }
                        }
                    },
                    { posts: List<Post> ->
                        updateUiState {
                            copy {
                                PostsUiState.status set PostsStatus.Success
                                PostsUiState.posts set posts.toImmutableList()
                            }
                        }
                    },
                )
        }
    }

    private fun PostsError.mapToUiError(): PostUiError {
        val errorId = randomProvider.mostSignificantBitsRandomUUID()
        return when (this) {
            PostsError.NoConnectivity -> PostUiError.NoConnectivity(errorId)
            PostsError.Network -> PostUiError.Network(errorId)
            PostsError.Server -> PostUiError.Server(errorId)
            PostsError.Unexpected -> PostUiError.Unexpected(errorId)
        }
    }

    private fun dismissError(errorId: Long) {
        updateUiState {
            if (error == null || errorId == 0L || error.id == errorId) {
                copy {
                    PostsUiState.status set PostsStatus.Success
                    PostsUiState.error set null
                }
            } else {
                this
            }
        }
    }
}
