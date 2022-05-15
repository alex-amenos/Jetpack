package com.alxnophis.jetpack.location.tracker.ui.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
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
) : BaseViewModel<LocationTrackerEvent, LocationTrackerState, LocationTrackerEffect>(initialState), DefaultLifecycleObserver {

    init {
        subscribeToUserLocation()
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        startTrackingUserLocation()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        stopTrackUserLocation()
    }

    override fun handleEvent(event: LocationTrackerEvent) {
        when (event) {
            LocationTrackerEvent.Finish -> setEffect { LocationTrackerEffect.Finish }
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

    companion object {
        private const val LOCATION_INTERVAL_MILLIS = 5000L
    }
}
