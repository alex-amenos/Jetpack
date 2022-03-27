package com.alxnophis.jetpack.settings.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.core.extensions.doNothing
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsSideEffect
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.contract.SettingsViewAction
import com.alxnophis.jetpack.settings.ui.contract.Theme
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    initialState: SettingsState = SettingsState(),
) : BaseViewModel<SettingsViewAction, SettingsState, SettingsSideEffect>(initialState) {

    override fun handleAction(action: SettingsViewAction) =
        when (action) {
            is SettingsViewAction.Finish -> setSideEffect { SettingsSideEffect.Finish }
            is SettingsViewAction.ManageSubscription -> manageSubscription()
            is SettingsViewAction.SetNotifications -> toggleNotifications()
            is SettingsViewAction.SetHint -> toggleHint()
            is SettingsViewAction.SetMarketingOption -> setMarketing(action.marketingOption)
            is SettingsViewAction.SetTheme -> setTheme(action.theme)
        }

    private fun manageSubscription() {
        doNothing()
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
