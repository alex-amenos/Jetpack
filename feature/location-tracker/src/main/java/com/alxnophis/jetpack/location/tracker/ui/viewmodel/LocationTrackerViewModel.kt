package com.alxnophis.jetpack.location.tracker.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.location.tracker.domain.usecase.GetUserLocationsFlowUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StartLocationRequestUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StopLocationRequestUseCase
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEffect
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerState
import kotlinx.coroutines.launch
import timber.log.Timber

internal class LocationTrackerViewModel(
    initialState: LocationTrackerState = LocationTrackerState(),
    private val startLocationRequestUseCase: StartLocationRequestUseCase,
    private val stopLocationRequestUseCase: StopLocationRequestUseCase,
    private val getUserLocationsFlowUseCase: GetUserLocationsFlowUseCase,
) : BaseViewModel<LocationTrackerEvent, LocationTrackerState, LocationTrackerEffect>(initialState) {

    init {
        subscribeToUserLocation()
    }

    override fun handleEvent(event: LocationTrackerEvent) {
        when (event) {
            LocationTrackerEvent.Finish -> setEffect { LocationTrackerEffect.Finish }
            LocationTrackerEvent.StartTrackingUserLocation -> startTrackingUserLocation()
            LocationTrackerEvent.StopTrackingUserLocation -> stopTrackUserLocation()
        }
    }

    private fun startTrackingUserLocation() = viewModelScope.launch {
        startLocationRequestUseCase(LOCATION_INTERVAL_MILLIS)
    }

    private fun stopTrackUserLocation() = viewModelScope.launch {
        stopLocationRequestUseCase()
    }

    private fun subscribeToUserLocation() = viewModelScope.launch {
        getUserLocationsFlowUseCase().collect { userLocation ->
            Timber.d("New location viewModel $userLocation")
            userLocation?.let {
                setState {
                    currentState.copy(userLocation = userLocation)
                }
            }
        }
    }

    companion object {
        private const val LOCATION_INTERVAL_MILLIS = 5000L
    }
}
