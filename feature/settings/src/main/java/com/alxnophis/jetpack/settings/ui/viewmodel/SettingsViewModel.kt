package com.alxnophis.jetpack.settings.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.extensions.doNothing
import com.alxnophis.jetpack.settings.data.repository.SettingsRepository
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsUiEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsUiState
import com.alxnophis.jetpack.settings.ui.contract.Theme
import com.alxnophis.jetpack.settings.ui.mapper.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    initialState: SettingsUiState = SettingsUiState.initialState,
) : ViewModel() {
    private val currentState: SettingsUiState
        get() = uiState.value

    val uiState =
        settingsRepository
            .getSettingsFlow()
            .map {
                SettingsUiState(
                    notificationsEnabled = it.notificationsEnabled,
                    hintsEnabled = it.hintsEnabled,
                    marketingOption =
                        when (it.marketingOption) {
                            true -> MarketingOption.ALLOWED
                            false -> MarketingOption.NOT_ALLOWED
                        },
                    themeOption = it.themeOption.map(),
                )
            }.stateIn(
                scope = viewModelScope,
                initialValue = initialState,
                started =
                    SharingStarted.WhileSubscribed(
                        stopTimeoutMillis = STOP_TIMEOUT_MILLIS,
                        replayExpirationMillis = REPLAY_EXPIRATION_MILLIS,
                    ),
            )

    fun handleEvent(event: SettingsUiEvent) {
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
        settingsRepository.updateNotificationsEnabled(currentState.notificationsEnabled.not())
    }

    private suspend fun toggleHint() {
        settingsRepository.updateHintsEnabled(currentState.hintsEnabled.not())
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

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 1_000L
        private const val REPLAY_EXPIRATION_MILLIS = 9_000L
    }
}
