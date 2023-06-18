package com.alxnophis.jetpack.posts.ui.contract

import arrow.optics.optics
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.posts.domain.model.Post

internal sealed class PostsEvent : UiEvent {
    object Initialize : PostsEvent()
    data class DismissError(val errorId: Long) : PostsEvent()
}

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
