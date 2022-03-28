package com.alxnophis.jetpack.posts.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiAction
import com.alxnophis.jetpack.core.base.viewmodel.UiEffect
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.posts.domain.model.Post

internal sealed class PostsSideEffect : UiEffect {
    object Finish : PostsSideEffect()
}

internal sealed class PostsViewAction : UiAction {
    object GetPosts : PostsViewAction()
    data class DismissError(val errorId: Long) : PostsViewAction()
}

internal data class PostsState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val errorMessages: List<ErrorMessage> = emptyList(),
) : UiState
