package com.alxnophis.jetpack.posts.ui.view.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.ui.contract.PostUiError
import com.alxnophis.jetpack.posts.ui.contract.PostsUiState

internal class PostStateProvider : PreviewParameterProvider<PostsUiState> {
    override val values =
        sequenceOf(
            PostsUiState(
                isLoading = false,
                posts = listOf(post1, post2),
                error = null,
            ),
            PostsUiState(
                isLoading = false,
                posts = emptyList(),
                error = PostUiError.Network,
            ),
            PostsUiState(
                isLoading = false,
                posts = emptyList(),
                error = PostUiError.Server,
            ),
            PostsUiState(
                isLoading = false,
                posts = emptyList(),
                error = PostUiError.Unknown,
            ),
            PostsUiState(
                isLoading = false,
                posts = emptyList(),
                error = PostUiError.Unexpected,
            ),
            PostsUiState(
                isLoading = true,
                posts = emptyList(),
                error = null,
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
