package com.alxnophis.jetpack.settings.ui.viewmodel

import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.core.extensions.doNothing
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsEffect
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.contract.Theme

internal class SettingsViewModel(
    initialState: SettingsState = SettingsState(),
) : BaseViewModel<SettingsEvent, SettingsState, SettingsEffect>(initialState) {

    override fun handleEvent(event: SettingsEvent) =
        when (event) {
            is SettingsEvent.NavigateBack -> setEffect { SettingsEffect.NavigateBack }
            is SettingsEvent.ManageSubscription -> manageSubscription()
            is SettingsEvent.SetNotifications -> toggleNotifications()
            is SettingsEvent.SetHint -> toggleHint()
            is SettingsEvent.SetMarketingOption -> setMarketing(event.marketingOption)
            is SettingsEvent.SetTheme -> setTheme(event.theme)
        }

    private fun manageSubscription() {
        doNothing()
    }

    private fun toggleNotifications() {
        setState {
            copy(notificationsEnabled = !this.notificationsEnabled)
        }
    }

    private fun toggleHint() {
        setState {
            copy(hintsEnabled = !this.hintsEnabled)
        }
    }

    private fun setMarketing(option: MarketingOption) {
        setState {
            copy(marketingOption = option)
        }
    }

    private fun setTheme(theme: Theme) {
        setState {
            copy(themeOption = theme)
        }
    }
}
