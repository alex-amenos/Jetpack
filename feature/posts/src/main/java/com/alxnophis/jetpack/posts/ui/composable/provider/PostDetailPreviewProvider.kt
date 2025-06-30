package com.alxnophis.jetpack.posts.ui.composable.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.ui.contract.PostDetailStatus
import com.alxnophis.jetpack.posts.ui.contract.PostDetailUiError
import com.alxnophis.jetpack.posts.ui.contract.PostDetailUiState

internal class PostDetailPreviewProvider : PreviewParameterProvider<PostDetailUiState> {
    override val values: Sequence<PostDetailUiState>
        get() =
            sequenceOf(
                PostDetailUiState.initialState,
                PostDetailUiState(
                    status = PostDetailStatus.Success,
                    post = post,
                    postId = post.id,
                    error = null,
                ),
                PostDetailUiState(
                    status = PostDetailStatus.Error,
                    post = null,
                    postId = post.id,
                    error = PostDetailUiError.NotFound,
                ),
                PostDetailUiState(
                    status = PostDetailStatus.Error,
                    post = null,
                    postId = post.id,
                    error = PostDetailUiError.Network,
                ),
                PostDetailUiState(
                    status = PostDetailStatus.Error,
                    post = null,
                    postId = post.id,
                    error = PostDetailUiError.Unknown,
                ),
                PostDetailUiState(
                    status = PostDetailStatus.Error,
                    post = null,
                    postId = post.id,
                    error = PostDetailUiError.Server,
                ),
                PostDetailUiState(
                    status = PostDetailStatus.Error,
                    post = null,
                    postId = post.id,
                    error = PostDetailUiError.Unexpected,
                ),
            )

    companion object {
        val post =
            Post(
                id = 1,
                userId = 1,
                title = "Lorem ipsum dolor",
                body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            )
    }
}
