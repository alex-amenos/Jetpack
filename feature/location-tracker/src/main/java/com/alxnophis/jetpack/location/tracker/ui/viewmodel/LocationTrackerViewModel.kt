package com.alxnophis.jetpack.location.tracker.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.location.tracker.domain.usecase.LocationStateUseCase
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
    private val locationStateUseCase: LocationStateUseCase,
) : BaseViewModel<LocationTrackerEvent, LocationTrackerState, LocationTrackerEffect>(initialState) {

    override fun handleEvent(event: LocationTrackerEvent) {
        when (event) {
            LocationTrackerEvent.FineLocationPermissionGranted -> {
                // TODO - Check if location is enabled
                startTrackingUserLocation()
                subscribeToUserLocation()
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
                currentState.copy(userLocation = locationState.toString())
            }
        }
    }

    override fun onCleared() {
        stopTrackUserLocation()
        super.onCleared()
    }
}
