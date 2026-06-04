package com.alxnophis.jetpack.location.tracker.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.updateCopy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.location.tracker.data.model.LocationParameters
import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiState
import com.alxnophis.jetpack.location.tracker.ui.contract.hasRequestedPermissions
import com.alxnophis.jetpack.location.tracker.ui.contract.isFineLocationPermissionGranted
import com.alxnophis.jetpack.location.tracker.ui.contract.isFollowingUser
import com.alxnophis.jetpack.location.tracker.ui.contract.lastKnownLocationData
import com.alxnophis.jetpack.location.tracker.ui.contract.userLocationData
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class LocationTrackerViewModel(
    private val locationRepository: LocationRepository,
    initialState: LocationTrackerUiState = LocationTrackerUiState.initialState,
) : BaseViewModel<LocationTrackerUiEvent, LocationTrackerUiState>(initialState) {
    override fun handleEvent(event: LocationTrackerUiEvent) {
        viewModelScope.launch {
            when (event) {
                LocationTrackerUiEvent.FineLocationPermissionGrantedUi -> {
                    permissionsGranted()
                    startTrackingUserLocation()
                    subscribeToUserLocation()
                    subscribeToLastKnownLocation()
                }

                LocationTrackerUiEvent.StopTrackingRequested -> {
                    stopTrackUserLocation()
                }

                LocationTrackerUiEvent.GoBackRequested -> {
                    throw IllegalStateException("GoBackRequested not implemented")
                }

                LocationTrackerUiEvent.MapDraggedByGesture -> {
                    _uiState.updateCopy {
                        LocationTrackerUiState.isFollowingUser set false
                    }
                }

                LocationTrackerUiEvent.FollowUserClicked -> {
                    _uiState.updateCopy {
                        LocationTrackerUiState.isFollowingUser set true
                    }
                }

                LocationTrackerUiEvent.PermissionRequested -> {
                    _uiState.updateCopy {
                        LocationTrackerUiState.hasRequestedPermissions set true
                    }
                }
            }
        }
    }

    private fun permissionsGranted() {
        _uiState.updateCopy {
            LocationTrackerUiState.isFineLocationPermissionGranted set true
        }
    }

    private fun startTrackingUserLocation() =
        viewModelScope.launch {
            locationRepository.startLocationProvider(LocationParameters())
        }

    private fun stopTrackUserLocation() =
        viewModelScope.launch {
            locationRepository.stopLocationProvider()
        }

    private fun subscribeToUserLocation() =
        viewModelScope.launch {
            locationRepository.locationSharedFlow.collectLatest { locationState ->
                _uiState.updateCopy {
                    LocationTrackerUiState.userLocationData set locationState
                }
            }
        }

    private fun subscribeToLastKnownLocation() =
        viewModelScope.launch {
            locationRepository
                .provideLastKnownLocationFlow()
                .collectLatest { lastKnownLocation ->
                    _uiState.updateCopy {
                        LocationTrackerUiState.lastKnownLocationData set lastKnownLocation
                    }
                }
        }

    override fun onCleared() {
        stopTrackUserLocation()
        super.onCleared()
    }
}
