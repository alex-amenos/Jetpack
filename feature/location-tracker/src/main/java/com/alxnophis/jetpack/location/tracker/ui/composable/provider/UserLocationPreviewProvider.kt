package com.alxnophis.jetpack.location.tracker.ui.composable.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiState

internal class UserLocationPreviewProvider : PreviewParameterProvider<LocationTrackerUiState> {
    override val values: Sequence<LocationTrackerUiState>
        get() =
            sequenceOf(
                LocationTrackerUiState(
                    isFineLocationPermissionGranted = true,
                    lastKnownLocation = "Last known location",
                    userLocation = "Current Location",
                ),
                LocationTrackerUiState(
                    isFineLocationPermissionGranted = false,
                    lastKnownLocation = "Last known location",
                    userLocation = "Current Location",
                ),
            )
}
