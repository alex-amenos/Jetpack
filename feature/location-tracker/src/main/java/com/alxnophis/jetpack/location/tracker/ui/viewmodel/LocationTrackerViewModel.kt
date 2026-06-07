package com.alxnophis.jetpack.location.tracker.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.location.tracker.data.model.LocationParameters
import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiState
import com.alxnophis.jetpack.location.tracker.ui.contract.hasLocationAccess
import com.alxnophis.jetpack.location.tracker.ui.contract.isFollowingUser
import com.alxnophis.jetpack.location.tracker.ui.contract.lastKnownLocationData
import com.alxnophis.jetpack.location.tracker.ui.contract.userLocationData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class LocationTrackerViewModel(
    private val locationRepository: LocationRepository,
    initialState: LocationTrackerUiState = LocationTrackerUiState.initialState,
) : BaseViewModel<LocationTrackerUiEvent, LocationTrackerUiState>(initialState) {
    private var userLocationJob: Job? = null
    private var lastKnownLocationJob: Job? = null

    override fun handleEvent(event: LocationTrackerUiEvent) {
        viewModelScope.launch {
            when (event) {
                LocationTrackerUiEvent.LocationAccessGranted -> {
                    locationAccessGranted()
                    subscribeToUserLocation()
                    subscribeToLastKnownLocation()
                }

                LocationTrackerUiEvent.LocationAccessRevoked -> {
                    locationAccessRevoked()
                    stopTrackUserLocation()
                }

                LocationTrackerUiEvent.StopTrackingRequested -> {
                    stopTrackUserLocation()
                }

                LocationTrackerUiEvent.GoBackRequested -> {
                    throw IllegalStateException("GoBackRequested not implemented")
                }

                LocationTrackerUiEvent.MapDraggedByGesture -> {
                    updateUiState {
                        copy {
                            LocationTrackerUiState.isFollowingUser set false
                        }
                    }
                }

                LocationTrackerUiEvent.FollowUserClicked -> {
                    updateUiState {
                        copy {
                            LocationTrackerUiState.isFollowingUser set true
                        }
                    }
                }
            }
        }
    }

    private fun locationAccessGranted() {
        updateUiState {
            copy {
                LocationTrackerUiState.hasLocationAccess set true
            }
        }
    }

    private fun locationAccessRevoked() {
        updateUiState {
            copy {
                LocationTrackerUiState.hasLocationAccess set false
                LocationTrackerUiState.userLocationData set null
                LocationTrackerUiState.lastKnownLocationData set null
            }
        }
    }

    private fun stopTrackUserLocation() {
        userLocationJob?.cancel()
        lastKnownLocationJob?.cancel()
    }

    private fun subscribeToUserLocation() {
        if (userLocationJob?.isActive == true) return
        userLocationJob =
            viewModelScope.launch {
                locationRepository
                    .getLocationFlow(LocationParameters())
                    .collectLatest { locationState ->
                        updateUiState {
                            copy {
                                LocationTrackerUiState.userLocationData set locationState
                            }
                        }
                    }
            }
    }

    private fun subscribeToLastKnownLocation() {
        if (lastKnownLocationJob?.isActive == true) return
        lastKnownLocationJob =
            viewModelScope.launch {
                locationRepository
                    .provideLastKnownLocationFlow()
                    .collectLatest { lastKnownLocation ->
                        updateUiState {
                            copy {
                                LocationTrackerUiState.lastKnownLocationData set lastKnownLocation
                            }
                        }
                    }
            }
        lastKnownLocationJob?.invokeOnCompletion {
            lastKnownLocationJob = null
        }
    }

    override fun onCleared() {
        stopTrackUserLocation()
        super.onCleared()
    }
}
