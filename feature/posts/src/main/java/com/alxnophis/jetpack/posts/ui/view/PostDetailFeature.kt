package com.alxnophis.jetpack.posts.ui.view

import androidx.compose.runtime.Composable
import com.alxnophis.jetpack.posts.data.model.Post

@Composable
fun PostDetailFeature(
    onBack: () -> Unit,
    post: Post,
) {
    PostDetailScreen(
        onNavigateBack = onBack,
        post = post,
    )

}
