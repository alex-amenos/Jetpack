package com.alxnophis.jetpack.location.tracker.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.location.tracker.domain.usecase.GetUserLocationsFlowUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StartLastKnownLocationRequestUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StopLastKnownLocationRequestUseCase
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEffect
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerState
import kotlinx.coroutines.launch
import timber.log.Timber

internal class LocationTrackerViewModel(
    initialState: LocationTrackerState = LocationTrackerState(),
    private val startLastKnownLocationRequestUseCase: StartLastKnownLocationRequestUseCase,
    private val stopLastKnownLocationRequestUseCase: StopLastKnownLocationRequestUseCase,
    private val getUserLocationsFlowUseCase: GetUserLocationsFlowUseCase,
) : BaseViewModel<LocationTrackerEvent, LocationTrackerState, LocationTrackerEffect>(initialState) {

    init {
        startTrackingUserLocation()
        subscribeToUserLocation()
    }

    override fun handleEvent(event: LocationTrackerEvent) {
        when (event) {
            LocationTrackerEvent.NavigateBack -> setEffect { LocationTrackerEffect.NavigateBack }
        }
    }

    private fun startTrackingUserLocation() = viewModelScope.launch {
        startLastKnownLocationRequestUseCase(LOCATION_INTERVAL_MILLIS)
    }

    private fun stopTrackUserLocation() = viewModelScope.launch {
        stopLastKnownLocationRequestUseCase()
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

    override fun onCleared() {
        stopTrackUserLocation()
        super.onCleared()
    }

    companion object {
        private const val LOCATION_INTERVAL_MILLIS = 5000L
    }
}
