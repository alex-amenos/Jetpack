package com.alxnophis.jetpack.posts.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.optics.updateCopy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.posts.data.model.PostDetailError
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.ui.contract.PostDetailEvent
import com.alxnophis.jetpack.posts.ui.contract.PostDetailStatus
import com.alxnophis.jetpack.posts.ui.contract.PostDetailUiError
import com.alxnophis.jetpack.posts.ui.contract.PostDetailUiState
import com.alxnophis.jetpack.posts.ui.contract.error
import com.alxnophis.jetpack.posts.ui.contract.post
import com.alxnophis.jetpack.posts.ui.contract.postId
import com.alxnophis.jetpack.posts.ui.contract.status
import kotlinx.coroutines.launch

internal class PostDetailViewModel(
    private val postsRepository: PostsRepository,
    initialUiState: PostDetailUiState = PostDetailUiState.initialState,
) : BaseViewModel<PostDetailEvent, PostDetailUiState>(initialUiState) {
    override fun handleEvent(event: PostDetailEvent) {
        viewModelScope.launch {
            when (event) {
                is PostDetailEvent.LoadPost -> loadPost(event.postId)
                is PostDetailEvent.UpdatePost -> updatePost()
                is PostDetailEvent.GoBackRequested -> throw IllegalStateException("Go back not implemented in ViewModel")
                is PostDetailEvent.DismissErrorRequested -> dismissError()
            }
        }
    }

    private suspend fun loadPost(postId: Int) {
        _uiState.updateCopy {
            PostDetailUiState.status set PostDetailStatus.Loading
            PostDetailUiState.error set null
            PostDetailUiState.postId set postId
        }
        updatePost()
    }

    private suspend fun updatePost() {
        either {
            ensureNotNull(currentState.postId) {
                PostDetailError.NotFound
            }
            postsRepository.getPostById(currentState.postId!!).bind()
        }.fold(
            { error ->
                _uiState.updateCopy {
                    PostDetailUiState.status set PostDetailStatus.Error
                    PostDetailUiState.error set mapPostsErrorToUiError(error)
                }
            },
            { post ->
                _uiState.updateCopy {
                    PostDetailUiState.status set PostDetailStatus.Success
                    PostDetailUiState.post set post
                    PostDetailUiState.error set null
                }
            },
        )
    }

    private fun dismissError() {
        _uiState.updateCopy {
            PostDetailUiState.status set PostDetailStatus.Success
            PostDetailUiState.error set null
        }
    }

    private fun mapPostsErrorToUiError(error: PostDetailError): PostDetailUiError =
        when (error) {
            PostDetailError.Network -> PostDetailUiError.Network
            PostDetailError.NotFound -> PostDetailUiError.NotFound
            PostDetailError.Server -> PostDetailUiError.Server
            PostDetailError.Unknown -> PostDetailUiError.Unknown
            PostDetailError.Unexpected -> PostDetailUiError.Unexpected
        }
}
