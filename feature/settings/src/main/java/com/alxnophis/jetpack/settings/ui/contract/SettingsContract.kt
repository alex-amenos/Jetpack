package com.alxnophis.jetpack.settings.ui.contract

import androidx.annotation.StringRes
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.settings.R

internal sealed class SettingsEvent : UiEvent {
    data object ManageSubscription : SettingsEvent()
    data object SetNotifications : SettingsEvent()
    data object SetHint : SettingsEvent()
    data object GoBackRequested : SettingsEvent()
    data class SetMarketingOption(val marketingOption: MarketingOption) : SettingsEvent()
    data class SetTheme(val theme: Theme) : SettingsEvent()
}

@optics
internal data class SettingsState(
    val notificationsEnabled: Boolean,
    val hintsEnabled: Boolean,
    val marketingOption: MarketingOption,
    val themeOption: Theme
) : UiState {
    internal companion object {
        val initialState = SettingsState(
            notificationsEnabled = false,
            hintsEnabled = false,
            marketingOption = MarketingOption.ALLOWED,
            themeOption = Theme.SYSTEM
        )
    }
}

internal enum class MarketingOption(val id: Int) {
    ALLOWED(0),
    NOT_ALLOWED(1)
}

internal enum class Theme(@StringRes val label: Int) {
    LIGHT(R.string.settings_light),
    DARK(R.string.settings_dark),
    SYSTEM(R.string.settings_system)
}
