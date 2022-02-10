package com.alxnophis.jetpack.settings.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.contract.Theme
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    initialState: SettingsState = SettingsState(),
) : BaseViewModel<SettingsEvent, SettingsState>(initialState) {

    override fun handleAction(action: SettingsEvent) =
        when (action) {
            is SettingsEvent.SetNotifications -> toggleNotifications()
            is SettingsEvent.SetHint -> toggleHint()
            is SettingsEvent.SetMarketingOption -> setMarketing(action.marketingOption)
            is SettingsEvent.SetTheme -> setTheme(action.theme)
        }

    private fun toggleNotifications() {
        viewModelScope.launch {
            setState {
                copy(notificationsEnabled = !this.notificationsEnabled)
            }
        }
    }

    private fun toggleHint() {
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
