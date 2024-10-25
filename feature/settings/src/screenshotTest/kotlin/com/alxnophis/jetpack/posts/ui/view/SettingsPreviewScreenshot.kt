package com.alxnophis.jetpack.posts.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.view.SettingsPreviewProvider
import com.alxnophis.jetpack.settings.ui.view.SettingsScreen

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
