package com.alxnophis.jetpack.posts.ui.view.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.posts.data.model.Post

internal class PostDetailProvider : PreviewParameterProvider<Post> {
    override val values: Sequence<Post>
        get() = sequenceOf(
            Post(
                id = 1,
                userId = 1,
                title = "Lorem ipsum dolor",
                body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            )
        )
}
