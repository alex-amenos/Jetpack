package com.alxnophis.jetpack.radio.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.radio.domain.model.RadioError
import com.alxnophis.jetpack.radio.domain.model.RadioStation
import com.alxnophis.jetpack.radio.domain.usecase.GetSpanishStationsUseCase
import com.alxnophis.jetpack.radio.playback.RadioPlayer
import com.alxnophis.jetpack.radio.ui.contract.RadioStationListStatus
import com.alxnophis.jetpack.radio.ui.contract.RadioStationListEvent
import com.alxnophis.jetpack.radio.ui.contract.RadioStationListUiState
import com.alxnophis.jetpack.radio.ui.contract.stations
import com.alxnophis.jetpack.radio.ui.contract.status
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

internal class RadioStationListViewModel(
    private val getSpanishStations: GetSpanishStationsUseCase,
    private val player: RadioPlayer,
    initialUiState: RadioStationListUiState = RadioStationListUiState.initialState,
) : BaseViewModel<RadioStationListEvent, RadioStationListUiState>(initialUiState) {
    init {
        player.connect()
        load()
    }

    override fun handleEvent(event: RadioStationListEvent) {
        when (event) {
            RadioStationListEvent.Retry -> load()
            is RadioStationListEvent.Select -> player.playQueue(currentUiState.stations, event.index)
        }
    }

    private fun load() {
        updateUiState {
            copy {
                RadioStationListUiState.status set RadioStationListStatus.Loading
            }
        }
        viewModelScope.launch {
            getSpanishStations().fold(
                ifLeft = { showError() },
                ifRight = { stations -> showStations(stations) },
            )
        }
    }

    private fun showStations(stations: List<RadioStation>) {
        updateUiState {
            copy {
                RadioStationListUiState.stations set stations.toImmutableList()
                RadioStationListUiState.status set if (stations.isEmpty()) RadioStationListStatus.Empty else RadioStationListStatus.Content
            }
        }
    }

    private fun showError() {
        updateUiState {
            copy {
                RadioStationListUiState.status set RadioStationListStatus.Error
            }
        }
    }
}
