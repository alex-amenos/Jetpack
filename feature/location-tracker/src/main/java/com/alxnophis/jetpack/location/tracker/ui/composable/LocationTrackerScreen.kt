package com.alxnophis.jetpack.location.tracker.ui.composable

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.kotlin.constants.ZERO_DOUBLE
import com.alxnophis.jetpack.location.tracker.R
import com.alxnophis.jetpack.location.tracker.ui.composable.provider.UserLocationPreviewProvider
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
internal fun LocationTrackerScreen(
    uiState: LocationTrackerUiState,
    onEvent: (LocationTrackerUiEvent) -> Unit,
) {
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions: Map<String, Boolean> ->
                if (permissions.values.all { it }) {
                    onEvent(LocationTrackerUiEvent.FineLocationPermissionGrantedUi)
                }
            },
        )
    val requestLocationPermissions: () -> Unit = {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )
    }
    LaunchedEffect(Unit) {
        requestLocationPermissions()
    }
    BackHandler {
        onEvent(LocationTrackerUiEvent.StopTrackingRequested)
        onEvent(LocationTrackerUiEvent.GoBackRequested)
    }
    AppTheme {
        MapComposable(
            state = uiState,
            onEvent = onEvent,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun MapComposable(
    state: LocationTrackerUiState,
    onEvent: (LocationTrackerUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locationData =
        remember(state.userLocationData, state.lastKnownLocationData) {
            state.userLocationData ?: state.lastKnownLocationData
        }
    val position = locationData?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(ZERO_DOUBLE, ZERO_DOUBLE)
    val cameraPositionState =
        rememberCameraPositionState {
            this.position = CameraPosition.fromLatLngZoom(position, 15f)
        }
    // Update camera position when location changes
    LaunchedEffect(position) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(position, 15f)
    }
    Box(modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = state.isFineLocationPermissionGranted),
            uiSettings =
                MapUiSettings(
                    myLocationButtonEnabled = true,
                    mapToolbarEnabled = false,
                    zoomControlsEnabled = false,
                ),
            contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        ) {
            if (locationData != null) {
                Marker(
                    state = MarkerState(position = position),
                    title = stringResource(id = R.string.location_tracker_current_location),
                    contentDescription = stringResource(id = R.string.location_tracker_current_location),
                    snippet = "${locationData.latitude}, ${locationData.longitude}",
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun UserLocationPreview(
    @PreviewParameter(UserLocationPreviewProvider::class) uiState: LocationTrackerUiState,
) {
    LocationTrackerScreen(
        uiState = uiState,
        onEvent = {},
    )
}
