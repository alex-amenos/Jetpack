package com.alxnophis.jetpack.settings.ui.contract

import androidx.annotation.StringRes
import com.alxnophis.jetpack.core.base.viewmodel.UiEffect
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.settings.R

internal sealed class SettingsEvent : UiEvent {
    data class SetNotifications(val areEnabled: Boolean) : SettingsEvent()
    data class SetHint(val areEnabled: Boolean) : SettingsEvent()
    data class SetMarketingOption(val marketingOption: MarketingOption) : SettingsEvent()
    data class SetTheme(val theme: Theme) : SettingsEvent()
}

internal sealed class SettingsEffect : UiEffect

internal data class SettingsState(
    val notificationsEnabled: Boolean = false,
    val hintsEnabled: Boolean = false,
    val marketingOption: MarketingOption = MarketingOption.ALLOWED,
    val themeOption: Theme = Theme.SYSTEM
) : UiState

internal enum class MarketingOption(val id: Int) {
    ALLOWED(0),
    NOT_ALLOWED(1)
}

internal enum class Theme(@StringRes val label: Int) {
    LIGHT(R.string.settings_light),
    DARK(R.string.settings_dark),
    SYSTEM(R.string.settings_system)
}
