package com.alxnophis.jetpack.posts.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.alxnophis.jetpack.posts.data.model.Post

@Preview(showBackground = true)
@Composable
private fun PostDetailPreview() {
    val post =
        Post(
            id = 1,
            userId = 1,
            title = "Lorem ipsum dolor",
            body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        )
    PostDetail(
        post = post,
        onNavigateBack = {},
    )
}
