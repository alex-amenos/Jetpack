package com.alxnophis.jetpack.settings.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.core.extensions.doNothing
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.contract.Theme
import com.alxnophis.jetpack.settings.ui.contract.hintsEnabled
import com.alxnophis.jetpack.settings.ui.contract.marketingOption
import com.alxnophis.jetpack.settings.ui.contract.notificationsEnabled
import com.alxnophis.jetpack.settings.ui.contract.themeOption
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    initialState: SettingsState = SettingsState.initialState
) : BaseViewModel<SettingsEvent, SettingsState>(initialState) {

    override fun handleEvent(event: SettingsEvent) {
        viewModelScope.launch {
            when (event) {
                SettingsEvent.ManageSubscription -> manageSubscription()
                SettingsEvent.SetNotifications -> toggleNotifications()
                SettingsEvent.SetHint -> toggleHint()
                SettingsEvent.GoBackRequested -> throw IllegalStateException("Go back is not implemented")
                is SettingsEvent.SetMarketingOption -> setMarketing(event.marketingOption)
                is SettingsEvent.SetTheme -> setTheme(event.theme)
            }
        }
    }

    private fun manageSubscription() {
        doNothing()
    }

    private fun toggleNotifications() {
        updateUiState {
            copy {
                SettingsState.notificationsEnabled transform { !it }
            }
        }
    }

    private fun toggleHint() {
        updateUiState {
            copy {
                SettingsState.hintsEnabled transform { !it }
            }
        }
    }

    private fun setMarketing(option: MarketingOption) {
        updateUiState {
            copy {
                SettingsState.marketingOption set option
            }
        }
    }

    private fun setTheme(theme: Theme) {
        updateUiState {
            copy {
                SettingsState.themeOption set theme
            }
        }
    }
}
