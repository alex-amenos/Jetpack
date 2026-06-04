package com.alxnophis.jetpack.location.tracker.ui.composable.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.kotlin.constants.ZERO_DOUBLE
import com.alxnophis.jetpack.kotlin.constants.ZERO_FLOAT
import com.alxnophis.jetpack.kotlin.constants.ZERO_LONG
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiState

internal class UserLocationPreviewProvider : PreviewParameterProvider<LocationTrackerUiState> {
    override val values: Sequence<LocationTrackerUiState>
        get() =
            sequenceOf(
                LocationTrackerUiState(
                    isFineLocationPermissionGranted = true,
                    userLocationData = DEFAULT_LOCATION,
                    lastKnownLocationData = DEFAULT_LOCATION,
                ),
                LocationTrackerUiState(
                    isFineLocationPermissionGranted = true,
                    userLocationData = null,
                    lastKnownLocationData = null,
                ),
                LocationTrackerUiState(
                    isFineLocationPermissionGranted = false,
                    userLocationData = null,
                    lastKnownLocationData = null,
                ),
            )

    companion object {
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
