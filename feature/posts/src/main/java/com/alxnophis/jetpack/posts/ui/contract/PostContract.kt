package com.alxnophis.jetpack.posts.ui.contract

import androidx.compose.runtime.Immutable
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.posts.data.model.Post

internal sealed interface PostsEvent : UiEvent {
    data object OnUpdatePostsRequested : PostsEvent

    data object GoBackRequested : PostsEvent

    data object DismissErrorRequested : PostsEvent

    data class OnPostClicked(val post: Post) : PostsEvent
}

@Immutable
internal data class PostsState(
    val isLoading: Boolean,
    val posts: List<Post>,
    val error: PostUiError?,
) : UiState {
    internal companion object {
        val initialState =
            PostsState(
                isLoading = false,
                posts = emptyList(),
                error = null,
            )
    }
}

sealed interface PostUiError {
    data object Network : PostUiError

    data object Server : PostUiError

    data object Unknown : PostUiError

    data object Unexpected : PostUiError
}
