package com.alxnophis.jetpack.settings.ui.composable

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsUiState
import com.alxnophis.jetpack.settings.ui.contract.Theme

internal class SettingsPreviewProvider : PreviewParameterProvider<SettingsUiState> {
    override val values: Sequence<SettingsUiState>
        get() =
            sequenceOf(
                SettingsUiState(
                    notificationsEnabled = false,
                    hintsEnabled = false,
                    marketingOption = MarketingOption.ALLOWED,
                    themeOption = Theme.SYSTEM,
                ),
                SettingsUiState(
                    notificationsEnabled = true,
                    hintsEnabled = true,
                    marketingOption = MarketingOption.NOT_ALLOWED,
                    themeOption = Theme.DARK,
                ),
                SettingsUiState(
                    notificationsEnabled = true,
                    hintsEnabled = true,
                    marketingOption = MarketingOption.NOT_ALLOWED,
                    themeOption = Theme.LIGHT,
                ),
            )
}
