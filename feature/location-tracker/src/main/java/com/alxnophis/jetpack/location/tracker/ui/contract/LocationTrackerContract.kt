package com.alxnophis.jetpack.location.tracker.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState

internal sealed class LocationTrackerEvent : UiEvent {
    object FineLocationPermissionGranted : LocationTrackerEvent()
    object EndTracking : LocationTrackerEvent()
}

internal data class LocationTrackerState(
    val userLocation: String? = null,
    val lastKnownLocation: String? = null
) : UiState
