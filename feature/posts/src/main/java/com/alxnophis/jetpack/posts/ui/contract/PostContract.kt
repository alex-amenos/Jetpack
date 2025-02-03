package com.alxnophis.jetpack.posts.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.posts.data.model.Post

internal sealed interface PostsEvent : UiEvent {
    data object OnUpdatePostsRequested : PostsEvent

    data object GoBackRequested : PostsEvent

    data object DismissErrorRequested : PostsEvent

    data class OnPostClicked(
        val post: Post,
    ) : PostsEvent
}

@Immutable
@optics
internal data class PostsUiState(
    val status: PostsStatus,
    val posts: List<Post>,
    val error: PostUiError?,
) : UiState {
    val isLoading: Boolean = status == PostsStatus.Loading

    internal companion object {
        val initialState =
            PostsUiState(
                status = PostsStatus.Loading,
                posts = emptyList(),
                error = null,
            )
    }
}

internal sealed interface PostsStatus {
    data object Loading : PostsStatus

    data object Success : PostsStatus

    data object Error : PostsStatus
}

internal sealed interface PostUiError {
    data object Network : PostUiError

    data object Server : PostUiError

    data object Unknown : PostUiError

    data object Unexpected : PostUiError
}
