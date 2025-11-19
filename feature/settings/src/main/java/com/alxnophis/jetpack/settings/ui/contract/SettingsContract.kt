package com.alxnophis.jetpack.settings.ui.contract

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.data.model.SettingsPreferences

internal sealed class SettingsUiEvent : UiEvent {
    data object ManageSubscription : SettingsUiEvent()

    data object SetNotifications : SettingsUiEvent()

    data object SetHint : SettingsUiEvent()

    data object GoBackRequested : SettingsUiEvent()

    data class SetMarketingOption(
        val marketingOption: MarketingOption,
    ) : SettingsUiEvent()

    data class SetTheme(
        val theme: Theme,
    ) : SettingsUiEvent()
}

@optics
@Immutable
internal data class SettingsUiState(
    val notificationsEnabled: Boolean,
    val hintsEnabled: Boolean,
    val marketingOption: MarketingOption,
    val themeOption: Theme,
) : UiState {
    internal companion object {
        val initialState =
            SettingsUiState(
                notificationsEnabled = false,
                hintsEnabled = false,
                marketingOption = MarketingOption.NOT_ALLOWED,
                themeOption = Theme.SYSTEM,
            )
    }
}

internal enum class MarketingOption(
    val id: Int,
) {
    ALLOWED(0),
    NOT_ALLOWED(1),
}

internal enum class Theme(
    @StringRes val label: Int,
) {
    LIGHT(R.string.settings_light),
    DARK(R.string.settings_dark),
    SYSTEM(R.string.settings_system),
    ;

    fun map(): SettingsPreferences.ThemeOptions =
        when (this) {
            LIGHT -> SettingsPreferences.ThemeOptions.LIGHT
            DARK -> SettingsPreferences.ThemeOptions.DARK
            SYSTEM -> SettingsPreferences.ThemeOptions.SYSTEM
        }
}
