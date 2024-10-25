package com.alxnophis.jetpack.posts.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.ui.view.provider.PostDetailProvider

internal class PostDetailsPreviewsScreenshots {

    @Preview(showBackground = true)
    @Composable
    private fun PostDetailScreenTesting(
        @PreviewParameter(PostDetailProvider::class) post: Post,
    ) {
        PostDetailScreen(post = post)
    }
}
