package com.alxnophis.jetpack.posts.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import com.alxnophis.jetpack.posts.data.model.Post

internal sealed interface PostDetailEvent : UiEvent {
    data class LoadPost(
        val postId: Int,
    ) : PostDetailEvent

    data object UpdatePost : PostDetailEvent

    data object GoBackRequested : PostDetailEvent

    data object DismissErrorRequested : PostDetailEvent
}

@optics
@Immutable
internal data class PostDetailUiState(
    val status: PostDetailStatus,
    val post: Post?,
    val postId: Int?,
    val error: PostDetailUiError?,
) : UiState {
    val isLoading: Boolean = status == PostDetailStatus.Loading
    val isSuccess: Boolean = status == PostDetailStatus.Success
    val isError: Boolean = status == PostDetailStatus.Error

    val postTitle: String
        get() = post?.titleCapitalized ?: EMPTY

    val postBody: String
        get() = post?.body ?: EMPTY

    internal companion object {
        val initialState =
            PostDetailUiState(
                status = PostDetailStatus.Loading,
                post = null,
                postId = null,
                error = null,
            )
    }
}

internal sealed interface PostDetailStatus {
    data object Loading : PostDetailStatus

    data object Success : PostDetailStatus

    data object Error : PostDetailStatus
}

internal sealed interface PostDetailUiError {
    data object Network : PostDetailUiError

    data object Server : PostDetailUiError

    data object NotFound : PostDetailUiError

    data object Unknown : PostDetailUiError

    data object Unexpected : PostDetailUiError
}
