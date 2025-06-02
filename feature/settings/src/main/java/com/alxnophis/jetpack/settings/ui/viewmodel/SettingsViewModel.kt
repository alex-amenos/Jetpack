package com.alxnophis.jetpack.settings.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.updateCopy
import com.alxnophis.jetpack.core.extensions.doNothing
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.settings.data.repository.SettingsRepository
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.contract.Theme
import com.alxnophis.jetpack.settings.ui.contract.hintsEnabled
import com.alxnophis.jetpack.settings.ui.contract.marketingOption
import com.alxnophis.jetpack.settings.ui.contract.notificationsEnabled
import com.alxnophis.jetpack.settings.ui.contract.themeOption
import com.alxnophis.jetpack.settings.ui.mapper.map
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    initialState: SettingsState = SettingsState.initialState,
) : BaseViewModel<SettingsEvent, SettingsState>(initialState) {
    init {
        restoreState()
    }

    private fun restoreState() {
        viewModelScope.launch {
            val settings = settingsRepository.getSettings()
            _uiState.updateCopy {
                SettingsState.notificationsEnabled set settings.notificationsEnabled
                SettingsState.hintsEnabled set settings.hintsEnabled
                SettingsState.marketingOption set
                    when (settings.marketingOption) {
                        true -> MarketingOption.ALLOWED
                        false -> MarketingOption.NOT_ALLOWED
                    }
                SettingsState.themeOption set settings.themeOption.map()
            }
        }
    }

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
        _uiState.updateCopy {
            SettingsState.notificationsEnabled transform { !it }
        }
        viewModelScope.launch {
            settingsRepository.updateNotificationsEnabled(currentState.notificationsEnabled)
        }
    }

    private fun toggleHint() {
        _uiState.updateCopy {
            SettingsState.hintsEnabled transform { !it }
        }
        viewModelScope.launch {
            settingsRepository.updateHintsEnabled(currentState.hintsEnabled)
        }
    }

    private fun setMarketing(option: MarketingOption) {
        _uiState.updateCopy {
            SettingsState.marketingOption set option
        }
        viewModelScope.launch {
            val isMarketingEnabled =
                when (option) {
                    MarketingOption.ALLOWED -> true
                    MarketingOption.NOT_ALLOWED -> false
                }
            settingsRepository.updateMarketingOption(isMarketingEnabled)
        }
    }

    private fun setTheme(theme: Theme) {
        _uiState.updateCopy {
            SettingsState.themeOption set theme
        }
        viewModelScope.launch {
            settingsRepository.updateThemeOption(theme.map())
        }
    }
}
