package com.alxnophis.jetpack.location.tracker.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.constants.BREAK_LINE
import com.alxnophis.jetpack.core.base.constants.COMA
import com.alxnophis.jetpack.core.base.constants.PARENTHESES_CLOSED
import com.alxnophis.jetpack.core.base.constants.PARENTHESES_OPENED
import com.alxnophis.jetpack.core.base.constants.WHITE_SPACE
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import com.alxnophis.jetpack.location.tracker.domain.usecase.LocationFlowUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.ProvideLastKnownLocationUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StartLocationRequestUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StopLocationRequestUseCase
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEffect
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class LocationTrackerViewModel(
    initialState: LocationTrackerState = LocationTrackerState(),
    private val startLocationRequestUseCase: StartLocationRequestUseCase,
    private val stopLocationRequestUseCase: StopLocationRequestUseCase,
    private val locationStateUseCase: LocationFlowUseCase,
    private val lastKnownLocationUseCase: ProvideLastKnownLocationUseCase
) : BaseViewModel<LocationTrackerEvent, LocationTrackerState, LocationTrackerEffect>(initialState) {

    override fun handleEvent(event: LocationTrackerEvent) {
        when (event) {
            LocationTrackerEvent.FineLocationPermissionGranted -> {
                // TODO - Check if location is enabled
                startTrackingUserLocation()
                subscribeToUserLocation()
                subscribeToLastKnownLocation()
            }
            LocationTrackerEvent.NavigateBack -> {
                setEffect { LocationTrackerEffect.NavigateBack }
                stopTrackUserLocation()
            }
        }
    }

    private fun startTrackingUserLocation() = viewModelScope.launch {
        startLocationRequestUseCase()
    }

    private fun stopTrackUserLocation() = viewModelScope.launch {
        stopLocationRequestUseCase()
    }

    private fun subscribeToUserLocation() = viewModelScope.launch {
        locationStateUseCase().collectLatest { locationState ->
            setState {
                currentState.copy(
                    userLocation = locationState.parseToString()
                )
            }
        }
    }

    private fun subscribeToLastKnownLocation() = viewModelScope.launch {
        lastKnownLocationUseCase().collectLatest { lastKnownLocation ->
            setState {
                currentState.copy(
                    lastKnownLocation = lastKnownLocation?.parseToString()
                )
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
