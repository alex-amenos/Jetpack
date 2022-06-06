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
import com.alxnophis.jetpack.location.tracker.domain.usecase.StartLocationProviderUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StopLocationProviderUseCase
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEffect
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class LocationTrackerViewModel(
    initialState: LocationTrackerState,
    private val startLocationProviderUseCase: StartLocationProviderUseCase,
    private val stopLocationProviderUseCase: StopLocationProviderUseCase,
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
        startLocationProviderUseCase()
    }

    private fun stopTrackUserLocation() = viewModelScope.launch {
        stopLocationProviderUseCase()
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
