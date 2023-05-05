package com.alxnophis.jetpack.location.tracker.ui.contract

import arrow.optics.optics
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.kotlin.constants.EMPTY

internal sealed class LocationTrackerEvent : UiEvent {
    object FineLocationPermissionGranted : LocationTrackerEvent()
    object EndTracking : LocationTrackerEvent()
}

@optics
internal data class LocationTrackerState(
    val userLocation: String = EMPTY,
    val lastKnownLocation: String = EMPTY
) : UiState {
    internal companion object
}
