package com.alxnophis.jetpack.posts.ui.contract

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.alxnophis.jetpack.core.ui.parceler.immutableListParceler
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.posts.data.model.Post
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

internal object ImmutablePostListParceler : Parceler<ImmutableList<Post>> by immutableListParceler()

internal sealed interface PostsEvent : UiEvent {
    data object OnUpdatePostsRequested : PostsEvent

    data object GoBackRequested : PostsEvent

    data object DismissErrorRequested : PostsEvent

    data class OnPostClicked(
        val post: Post,
    ) : PostsEvent
}

@Parcelize
@Immutable
@TypeParceler<ImmutableList<Post>, ImmutablePostListParceler>
internal data class PostsUiState(
    val status: PostsStatus,
    val posts: ImmutableList<Post>,
    val error: PostUiError?,
) : UiState,
    Parcelable {
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

@Parcelize
@Immutable
internal sealed interface PostsStatus : Parcelable {
    data object Loading : PostsStatus

    data object Success : PostsStatus

    data object Error : PostsStatus
}

@Parcelize
@Immutable
internal sealed interface PostUiError : Parcelable {
    data object NoConnectivity : PostUiError

    data object Network : PostUiError

    data object NotFound : PostUiError

    data object Server : PostUiError

    data object Unexpected : PostUiError
}
