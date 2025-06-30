package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.alxnophis.jetpack.posts.ui.composable.provider.PostStatePreviewProvider
import com.alxnophis.jetpack.posts.ui.contract.PostsUiState
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun PostScreenTesting(
    @PreviewParameter(PostStatePreviewProvider::class) state: PostsUiState,
) {
    PostsScreen(state)
}
