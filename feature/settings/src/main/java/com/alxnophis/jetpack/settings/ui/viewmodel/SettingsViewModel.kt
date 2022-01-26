package com.alxnophis.jetpack.settings.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsEffect
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.contract.Theme
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    initialState: SettingsState = SettingsState(),
) : BaseViewModel<SettingsEvent, SettingsState, SettingsEffect>(initialState) {

    override fun handleEvent(event: SettingsEvent) =
        when (event) {
            is SettingsEvent.SetNotifications -> toogleNotifications()
            is SettingsEvent.SetHint -> toogleHint()
            is SettingsEvent.SetMarketingOption -> setMarketing(event.marketingOption)
            is SettingsEvent.SetTheme -> setTheme(event.theme)
        }

    private fun toogleNotifications() {
        viewModelScope.launch {
            setState {
                copy(notificationsEnabled = !this.notificationsEnabled)
            }
        }
    }

    private fun toogleHint() {
        viewModelScope.launch {
            setState {
                copy(hintsEnabled = !this.hintsEnabled)
            }
        }
    }

    private fun setMarketing(option: MarketingOption) {
        viewModelScope.launch {
            setState {
                copy(marketingOption = option)
            }
        }
    }

    private fun setTheme(theme: Theme) {
        viewModelScope.launch {
            setState {
                copy(themeOption = theme)
            }
        }
    }
}
