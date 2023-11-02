package com.alxnophis.jetpack.posts.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.posts.data.model.Post

internal sealed interface PostsEvent : UiEvent {
    object Initialized : PostsEvent
    object GoBackRequested : PostsEvent
    data class DismissErrorRequested(val errorId: Long) : PostsEvent
}

@Immutable
@optics
internal data class PostsState(
    val isLoading: Boolean,
    val posts: List<Post>,
    val errorMessages: List<ErrorMessage>
) : UiState {
    internal companion object {
        val initialState = PostsState(
            isLoading = false,
            posts = emptyList(),
            errorMessages = emptyList()
        )
    }
}
