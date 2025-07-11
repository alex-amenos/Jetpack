package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.alxnophis.jetpack.posts.ui.composable.provider.PostDetailPreviewProvider
import com.alxnophis.jetpack.posts.ui.contract.PostDetailUiState
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun PostDetailScreenTesting(
    @PreviewParameter(PostDetailPreviewProvider::class) uiState: PostDetailUiState,
) {
    PostDetailScreen(uiState)
}
