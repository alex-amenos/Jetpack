package com.alxnophis.jetpack.root.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.connectivity.NetworkMonitor
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.root.ui.contract.RootUiEvent
import com.alxnophis.jetpack.root.ui.contract.RootUiState
import com.alxnophis.jetpack.settings.data.repository.SettingsRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class RootViewModel(
    private val settingsRepository: SettingsRepository,
    private val networkMonitor: NetworkMonitor,
    initialState: RootUiState = RootUiState.initialState,
) : BaseViewModel<RootUiEvent, RootUiState>(initialState) {

    init {
        viewModelScope.launch {
            settingsRepository
                .getSettingsFlow()
                .map { it.themeOption }
                .distinctUntilChanged()
                .collect { theme ->
                    updateUiState {
                        copy(themeOption = theme)
                    }
                }
        }
        viewModelScope.launch {
            networkMonitor
                .isOnline
                .distinctUntilChanged()
                .collect { isOnline ->
                    updateUiState {
                        copy(isOnline = isOnline)
                    }
                }
        }
    }

    override fun handleEvent(event: RootUiEvent) {
        // Root has no interactive UI events
    }
}
