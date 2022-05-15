package com.alxnophis.jetpack.posts.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEffect
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.posts.domain.model.Post

internal sealed class PostsEffect : UiEffect

internal sealed class PostsEvent : UiEvent {
    object GetPosts : PostsEvent()
    data class DismissError(val errorId: Long) : PostsEvent()
}

internal data class PostsState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val errorMessages: List<ErrorMessage> = emptyList(),
) : UiState
