package com.alxnophis.jetpack.location.tracker.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.base.constants.BREAK_LINE
import com.alxnophis.jetpack.core.base.constants.COMA
import com.alxnophis.jetpack.core.base.constants.PARENTHESES_CLOSED
import com.alxnophis.jetpack.core.base.constants.PARENTHESES_OPENED
import com.alxnophis.jetpack.core.base.constants.WHITE_SPACE
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import com.alxnophis.jetpack.location.tracker.domain.usecase.LocationFlowUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.ProvideLastKnownLocationUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StartLocationProviderUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StopLocationProviderUseCase
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerState
import com.alxnophis.jetpack.location.tracker.ui.contract.lastKnownLocation
import com.alxnophis.jetpack.location.tracker.ui.contract.userLocation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class LocationTrackerViewModel(
    private val startLocationProviderUseCase: StartLocationProviderUseCase,
    private val stopLocationProviderUseCase: StopLocationProviderUseCase,
    private val locationStateUseCase: LocationFlowUseCase,
    private val lastKnownLocationUseCase: ProvideLastKnownLocationUseCase,
    initialState: LocationTrackerState = LocationTrackerState.initialState
) : BaseViewModel<LocationTrackerEvent, LocationTrackerState>(initialState) {

    override fun handleEvent(event: LocationTrackerEvent) {
        viewModelScope.launch {
            when (event) {
                LocationTrackerEvent.FineLocationPermissionGranted -> {
                    // TODO - Check if location is enabled
                    startTrackingUserLocation()
                    subscribeToUserLocation()
                    subscribeToLastKnownLocation()
                }
                LocationTrackerEvent.StopTrackingRequested -> {
                    stopTrackUserLocation()
                }
            }
        }
    }

    private fun startTrackingUserLocation() = viewModelScope.launch {
        startLocationProviderUseCase()
    }

    private fun stopTrackUserLocation() = viewModelScope.launch {
        stopLocationProviderUseCase()
    }

    private fun subscribeToUserLocation() = viewModelScope.launch {
        locationStateUseCase().collectLatest { locationState ->
            updateUiState {
                copy {
                    LocationTrackerState.userLocation set locationState.parseToString()
                }
            }
        }
    }

    private fun subscribeToLastKnownLocation() = viewModelScope.launch {
        lastKnownLocationUseCase().collectLatest { lastKnownLocation ->
            lastKnownLocation?.let { location ->
                updateUiState {
                    copy {
                        LocationTrackerState.lastKnownLocation set location.parseToString()
                    }
                }
            }
        }
    }

    private fun Location.parseToString() = this
        .toString()
        .replace(PARENTHESES_OPENED, "($BREAK_LINE$WHITE_SPACE")
        .replace(PARENTHESES_CLOSED, "$BREAK_LINE$PARENTHESES_CLOSED")
        .replace(COMA, "$COMA$BREAK_LINE")

    override fun onCleared() {
        stopTrackUserLocation()
        super.onCleared()
    }
}
