package com.alxnophis.jetpack.posts.ui.view.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.ui.contract.PostUiError
import com.alxnophis.jetpack.posts.ui.contract.PostsStatus
import com.alxnophis.jetpack.posts.ui.contract.PostsUiState

internal class PostStatePreviewProvider : PreviewParameterProvider<PostsUiState> {
    override val values =
        sequenceOf(
            PostsUiState(
                status = PostsStatus.Success,
                posts = listOf(post1, post2),
                error = null,
            ),
            PostsUiState(
                status = PostsStatus.Loading,
                posts = emptyList(),
                error = null,
            ),
            PostsUiState(
                status = PostsStatus.Error,
                posts = emptyList(),
                error = PostUiError.Network,
            ),
            PostsUiState(
                status = PostsStatus.Error,
                posts = emptyList(),
                error = PostUiError.Server,
            ),
            PostsUiState(
                status = PostsStatus.Error,
                posts = emptyList(),
                error = PostUiError.Unknown,
            ),
            PostsUiState(
                status = PostsStatus.Error,
                posts = emptyList(),
                error = PostUiError.Unexpected,
            ),
        )

    companion object {
        val post1 =
            Post(
                id = 1,
                userId = 1,
                title = "Title 1",
                body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            )
        val post2 =
            Post(
                id = 2,
                userId = 1,
                title = "Title 2",
                body =
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            )
    }
}
