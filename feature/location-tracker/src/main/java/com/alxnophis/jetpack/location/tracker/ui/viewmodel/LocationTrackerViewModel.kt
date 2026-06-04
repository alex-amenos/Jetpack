package com.alxnophis.jetpack.location.tracker.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.updateCopy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.kotlin.constants.ZERO_DOUBLE
import com.alxnophis.jetpack.kotlin.constants.ZERO_FLOAT
import com.alxnophis.jetpack.kotlin.constants.ZERO_LONG
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import com.alxnophis.jetpack.location.tracker.domain.usecase.LocationFlowUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.ProvideLastKnownLocationUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StartLocationProviderUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StopLocationProviderUseCase
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiState
import com.alxnophis.jetpack.location.tracker.ui.contract.isFineLocationPermissionGranted
import com.alxnophis.jetpack.location.tracker.ui.contract.lastKnownLocationData
import com.alxnophis.jetpack.location.tracker.ui.contract.userLocationData
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class LocationTrackerViewModel(
    private val startLocationProviderUseCase: StartLocationProviderUseCase,
    private val stopLocationProviderUseCase: StopLocationProviderUseCase,
    private val locationStateUseCase: LocationFlowUseCase,
    private val lastKnownLocationUseCase: ProvideLastKnownLocationUseCase,
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
            startLocationProviderUseCase()
        }

    private fun stopTrackUserLocation() =
        viewModelScope.launch {
            stopLocationProviderUseCase()
        }

    private fun subscribeToUserLocation() =
        viewModelScope.launch {
            locationStateUseCase().collectLatest { locationState ->
                _uiState.updateCopy {
                    LocationTrackerUiState.userLocationData set locationState
                }
            }
        }

    private fun subscribeToLastKnownLocation() =
        viewModelScope.launch {
            lastKnownLocationUseCase().collectLatest { lastKnownLocation ->
                _uiState.updateCopy {
                    LocationTrackerUiState.lastKnownLocationData set lastKnownLocation ?: DEFAULT_LOCATION
                }
            }
        }

    override fun onCleared() {
        stopTrackUserLocation()
        super.onCleared()
    }

    private companion object {
        val DEFAULT_LOCATION =
            Location(
                latitude = ZERO_DOUBLE,
                longitude = ZERO_DOUBLE,
                accuracy = ZERO_FLOAT,
                altitude = ZERO_DOUBLE,
                speed = ZERO_FLOAT,
                bearing = ZERO_FLOAT,
                time = ZERO_LONG,
            )
    }
}
