package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.alxnophis.jetpack.settings.ui.composable.SettingsPreviewProvider
import com.alxnophis.jetpack.settings.ui.composable.SettingsScreen
import com.alxnophis.jetpack.settings.ui.contract.SettingsState

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(SettingsPreviewProvider::class) uiState: SettingsState,
) {
    SettingsScreen(
        state = uiState,
        appVersion = "1.0.0",
    )
}
