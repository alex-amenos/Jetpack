package com.alxnophis.jetpack.posts.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.posts.data.model.Post
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal sealed interface PostsEvent : UiEvent {
    data object OnUpdatePostsRequested : PostsEvent

    data class DismissErrorRequested(
        val errorId: Long = 0L,
    ) : PostsEvent
}

@optics
@Immutable
internal data class PostsUiState(
    val status: PostsStatus,
    val posts: ImmutableList<Post>,
    val error: PostUiError?,
) : UiState {
    val isLoading: Boolean = status == PostsStatus.Loading

    internal companion object {
        val initialState =
            PostsUiState(
                status = PostsStatus.Loading,
                posts = emptyList<Post>().toImmutableList(),
                error = null,
            )
    }
}

@Immutable
internal sealed interface PostsStatus {
    data object Loading : PostsStatus

    data object Success : PostsStatus

    data object Error : PostsStatus
}

@Immutable
internal sealed interface PostUiError {
    val id: Long

    data class NoConnectivity(
        override val id: Long = 0L,
    ) : PostUiError

    data class Network(
        override val id: Long = 0L,
    ) : PostUiError

    data class NotFound(
        override val id: Long = 0L,
    ) : PostUiError

    data class Server(
        override val id: Long = 0L,
    ) : PostUiError

    data class Unexpected(
        override val id: Long = 0L,
    ) : PostUiError
}
