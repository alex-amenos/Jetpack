package com.alxnophis.jetpack.posts.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.optics.updateCopy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.kotlin.constants.VIEW_MODEL_STOP_TIMEOUT_MILLIS
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class PostsViewModel(
    private val postsRepository: PostsRepository,
    initialState: PostsUiState = PostsUiState.initialState,
) : BaseViewModel<PostsEvent, PostsUiState>(initialState) {
    override val uiState: StateFlow<PostsUiState> =
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
            _uiState.updateCopy {
                PostsUiState.status set PostsStatus.Loading
            }
            getPosts().fold(
                { error ->
                    _uiState.updateCopy {
                        PostsUiState.status set PostsStatus.Error
                        PostsUiState.error set error.mapToUiError()
                    }
                },
                { posts: List<Post> ->
                    _uiState.updateCopy {
                        PostsUiState.status set PostsStatus.Success
                        PostsUiState.posts set posts
                    }
                },
            )
        }
    }

    private suspend fun getPosts(): Either<PostsError, List<Post>> = postsRepository.getPosts()

    private fun PostsError.mapToUiError(): PostUiError =
        when (this) {
            PostsError.Network -> PostUiError.Network
            PostsError.Server -> PostUiError.Server
            PostsError.Unknown -> PostUiError.Unknown
            PostsError.Unexpected -> PostUiError.Unexpected
        }

    private fun dismissError() {
        _uiState.updateCopy {
            PostsUiState.status set PostsStatus.Success
            PostsUiState.error set null
        }
    }
}
