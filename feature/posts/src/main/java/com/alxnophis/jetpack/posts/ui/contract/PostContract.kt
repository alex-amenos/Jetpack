package com.alxnophis.jetpack.posts.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.posts.data.model.Post

internal sealed interface PostsEvent : UiEvent {
    data object OnUpdatePostRequested : PostsEvent

    data object GoBackRequested : PostsEvent

    data class DismissErrorRequested(val errorId: Long) : PostsEvent

    data class OnPostClicked(val post: Post) : PostsEvent
}

@Immutable
@optics
internal data class PostsState(
    val isLoading: Boolean,
    val posts: List<Post>,
    val errorMessages: List<ErrorMessage>,
) : UiState {
    internal companion object {
        val initialState =
            PostsState(
                isLoading = false,
                posts = emptyList(),
                errorMessages = emptyList(),
            )
    }
}
