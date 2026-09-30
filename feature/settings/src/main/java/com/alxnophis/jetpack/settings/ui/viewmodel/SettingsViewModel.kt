package com.alxnophis.jetpack.settings.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.extensions.doNothing
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.settings.data.model.SettingsPreferences
import com.alxnophis.jetpack.settings.data.repository.SettingsRepository
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsUiEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsUiState
import com.alxnophis.jetpack.settings.ui.contract.Theme
import com.alxnophis.jetpack.settings.ui.mapper.map
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    initialState: SettingsUiState = SettingsUiState.initialState,
) : BaseViewModel<SettingsUiEvent, SettingsUiState>(initialState) {

    init {
        viewModelScope.launch {
            settingsRepository
                .getSettingsFlow()
                .collect { settings: SettingsPreferences ->
                    updateUiState {
                        copy(
                            notificationsEnabled = settings.notificationsEnabled,
                            hintsEnabled = settings.hintsEnabled,
                            marketingOption =
                                when (settings.marketingOption) {
                                    true -> MarketingOption.ALLOWED
                                    false -> MarketingOption.NOT_ALLOWED
                                },
                            themeOption = settings.themeOption.map(),
                        )
                    }
                }
        }
    }

    override fun handleEvent(event: SettingsUiEvent) {
        viewModelScope.launch {
            when (event) {
                SettingsUiEvent.ManageSubscription -> manageSubscription()
                SettingsUiEvent.SetNotifications -> toggleNotifications()
                SettingsUiEvent.SetHint -> toggleHint()
                SettingsUiEvent.GoBackRequested -> throw IllegalStateException("Go back is not implemented")
                is SettingsUiEvent.SetMarketingOption -> setMarketing(event.marketingOption)
                is SettingsUiEvent.SetTheme -> setTheme(event.theme)
            }
        }
    }

    private fun manageSubscription() {
        doNothing()
    }

    private suspend fun toggleNotifications() {
        settingsRepository.updateNotificationsEnabled(currentUiState.notificationsEnabled.not())
    }

    private suspend fun toggleHint() {
        settingsRepository.updateHintsEnabled(currentUiState.hintsEnabled.not())
    }

    private suspend fun setMarketing(option: MarketingOption) {
        val isMarketingEnabled =
            when (option) {
                MarketingOption.ALLOWED -> true
                MarketingOption.NOT_ALLOWED -> false
            }
        settingsRepository.updateMarketingOption(isMarketingEnabled)
    }

    private suspend fun setTheme(theme: Theme) {
        settingsRepository.updateThemeOption(theme.map())
    }
}
