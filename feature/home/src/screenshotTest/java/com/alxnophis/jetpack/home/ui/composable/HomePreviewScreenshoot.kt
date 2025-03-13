package com.alxnophis.jetpack.home.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.alxnophis.jetpack.home.ui.composable.provider.HomePreviewProvider
import com.alxnophis.jetpack.home.ui.contract.HomeState

@Preview
@Composable
private fun HomeScreenPreview(
    @PreviewParameter(HomePreviewProvider::class) uiState: HomeState,
) {
    HomeScreen(uiState)
}
