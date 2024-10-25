package com.alxnophis.jetpack.posts.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.alxnophis.jetpack.posts.ui.contract.PostsUiState
import com.alxnophis.jetpack.posts.ui.view.provider.PostStateProvider

@Preview(showBackground = true)
@Composable
private fun PostScreenTesting(
    @PreviewParameter(PostStateProvider::class) state: PostsUiState,
) {
    PostsScreen(state)
}
