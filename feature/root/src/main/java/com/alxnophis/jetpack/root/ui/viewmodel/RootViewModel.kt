package com.alxnophis.jetpack.root.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import com.alxnophis.jetpack.core.connectivity.NetworkMonitor
import com.alxnophis.jetpack.root.ui.contract.RootUiState
import com.alxnophis.jetpack.settings.data.repository.SettingsRepository

internal class RootViewModel(
    private val settingsRepository: SettingsRepository,
    private val networkMonitor: NetworkMonitor,
    initialState: RootUiState = RootUiState.initialState,
) : ViewModel() {

    val uiState: StateFlow<RootUiState> =
        combine(
            settingsRepository.getSettingsFlow(),
            networkMonitor.isOnline,
        ) { settings, isOnline ->
            RootUiState(
                themeOption = settings.themeOption,
                isOnline = isOnline,
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

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 1_000L
        private const val REPLAY_EXPIRATION_MILLIS = 9_000L
    }
}
