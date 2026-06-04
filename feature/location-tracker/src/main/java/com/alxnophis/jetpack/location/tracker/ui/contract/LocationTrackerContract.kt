package com.alxnophis.jetpack.location.tracker.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.location.tracker.data.model.Location

internal sealed class LocationTrackerUiEvent : UiEvent {
    data object FineLocationPermissionGrantedUi : LocationTrackerUiEvent()

    data object StopTrackingRequested : LocationTrackerUiEvent()

    data object GoBackRequested : LocationTrackerUiEvent()

    data object MapDraggedByGesture : LocationTrackerUiEvent()

    data object FollowUserClicked : LocationTrackerUiEvent()

    data object PermissionRequested : LocationTrackerUiEvent()
}

@optics
@Immutable
internal data class LocationTrackerUiState(
    val isFineLocationPermissionGranted: Boolean,
    val isFollowingUser: Boolean,
    val hasRequestedPermissions: Boolean,
    val lastKnownLocationData: Location?,
    val userLocationData: Location?,
) : UiState {
    internal companion object {
        val initialState =
            LocationTrackerUiState(
                isFineLocationPermissionGranted = false,
                isFollowingUser = true,
                hasRequestedPermissions = false,
                lastKnownLocationData = null,
                userLocationData = null,
            )
    }
}
