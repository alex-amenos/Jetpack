package com.alxnophis.jetpack.settings.ui.contract

import androidx.annotation.StringRes
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.settings.R

internal data class SettingsState(
    val notificationsEnabled: Boolean = false,
    val hintsEnabled: Boolean = false,
    val marketingOption: MarketingOption = MarketingOption.ALLOWED,
    val themeOption: Theme = Theme.SYSTEM
) : UiState

enum class MarketingOption(val id: Int) {
    ALLOWED(0),
    NOT_ALLOWED(1)
}

enum class Theme(@StringRes val label: Int) {
    LIGHT(R.string.settings_light),
    DARK(R.string.settings_dark),
    SYSTEM(R.string.settings_system)
}
