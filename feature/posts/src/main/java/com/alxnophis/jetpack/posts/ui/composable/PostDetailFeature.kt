package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.runtime.Composable

@Composable
fun PostDetailFeature(
    postId: Int,
    onBack: () -> Unit,
) {
    PostDetailScreen(
        onNavigateBack = onBack,
        postId = postId,
    )
}
