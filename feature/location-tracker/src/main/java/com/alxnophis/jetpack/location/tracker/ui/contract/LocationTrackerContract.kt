package com.alxnophis.jetpack.location.tracker.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.kotlin.constants.EMPTY

internal sealed class LocationTrackerUiEvent : UiEvent {
    data object FineLocationPermissionGrantedUi : LocationTrackerUiEvent()

    data object StopTrackingRequested : LocationTrackerUiEvent()

    data object GoBackRequested : LocationTrackerUiEvent()
}

@optics
@Immutable
internal data class LocationTrackerUiState(
    val isFineLocationPermissionGranted: Boolean,
    val userLocation: String,
    val lastKnownLocation: String,
) : UiState {
    internal companion object {
        val initialState =
            LocationTrackerUiState(
                isFineLocationPermissionGranted = false,
                userLocation = EMPTY,
                lastKnownLocation = EMPTY,
            )
    }
}
