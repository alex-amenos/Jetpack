package com.alxnophis.jetpack.settings.ui.composable

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.contract.Theme

internal class SettingsPreviewProvider : PreviewParameterProvider<SettingsState> {
    override val values: Sequence<SettingsState>
        get() =
            sequenceOf(
                SettingsState(
                    notificationsEnabled = false,
                    hintsEnabled = false,
                    marketingOption = MarketingOption.ALLOWED,
                    themeOption = Theme.SYSTEM,
                ),
                SettingsState(
                    notificationsEnabled = true,
                    hintsEnabled = true,
                    marketingOption = MarketingOption.NOT_ALLOWED,
                    themeOption = Theme.DARK,
                ),
                SettingsState(
                    notificationsEnabled = true,
                    hintsEnabled = true,
                    marketingOption = MarketingOption.NOT_ALLOWED,
                    themeOption = Theme.LIGHT,
                ),
            )
}
