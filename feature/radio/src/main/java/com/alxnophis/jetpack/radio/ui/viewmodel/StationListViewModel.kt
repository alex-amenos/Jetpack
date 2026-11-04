package com.alxnophis.jetpack.radio.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.radio.domain.model.RadioError
import com.alxnophis.jetpack.radio.domain.model.Station
import com.alxnophis.jetpack.radio.domain.usecase.GetSpanishStationsUseCase
import com.alxnophis.jetpack.radio.playback.RadioPlayer
import com.alxnophis.jetpack.radio.ui.contract.ListStatus
import com.alxnophis.jetpack.radio.ui.contract.StationListEvent
import com.alxnophis.jetpack.radio.ui.contract.StationListUiState
import com.alxnophis.jetpack.radio.ui.contract.stations
import com.alxnophis.jetpack.radio.ui.contract.status
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

internal class StationListViewModel(
    private val getSpanishStations: GetSpanishStationsUseCase,
    private val player: RadioPlayer,
    initialUiState: StationListUiState = StationListUiState.initialState,
) : BaseViewModel<StationListEvent, StationListUiState>(initialUiState) {
    init {
        player.connect()
        load()
    }

    override fun handleEvent(event: StationListEvent) {
        when (event) {
            StationListEvent.Retry -> load()
            is StationListEvent.Select -> player.playQueue(currentUiState.stations, event.index)
        }
    }

    private fun load() {
        updateUiState {
            copy {
                StationListUiState.status set ListStatus.Loading
            }
        }
        viewModelScope.launch {
            getSpanishStations().fold(
                ifLeft = { showError() },
                ifRight = { stations -> showStations(stations) },
            )
        }
    }

    private fun showStations(stations: List<Station>) {
        updateUiState {
            copy {
                StationListUiState.stations set stations.toImmutableList()
                StationListUiState.status set if (stations.isEmpty()) ListStatus.Empty else ListStatus.Content
            }
        }
    }

    private fun showError() {
        updateUiState {
            copy {
                StationListUiState.status set ListStatus.Error
            }
        }
    }
}
