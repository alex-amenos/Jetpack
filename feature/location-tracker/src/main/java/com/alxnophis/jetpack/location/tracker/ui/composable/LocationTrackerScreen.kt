package com.alxnophis.jetpack.location.tracker.ui.composable

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.kotlin.constants.ZERO_DOUBLE
import com.alxnophis.jetpack.location.tracker.R
import com.alxnophis.jetpack.location.tracker.ui.composable.provider.UserLocationPreviewProvider
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
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
    val isInspectionMode = LocalInspectionMode.current
    val locationData =
        remember(state.userLocationData, state.lastKnownLocationData) {
            state.userLocationData ?: state.lastKnownLocationData
        }
    val position = locationData?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(ZERO_DOUBLE, ZERO_DOUBLE)
    val cameraPositionState =
        rememberCameraPositionState {
            this.position = CameraPosition.fromLatLngZoom(position, 15f)
        }
    var isFollowingUser by rememberSaveable { mutableStateOf(true) }
    // Stop following if user drags map
    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            isFollowingUser = false
        }
    }
    // Update camera position when location changes
    LaunchedEffect(position) {
        if (isFollowingUser) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(position, 15f)
        }
    }
    Box(modifier) {
        if (isInspectionMode) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
            ) {
                if (locationData != null) {
                    UserLocationMarkerContent(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        } else {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = false),
                uiSettings =
                    MapUiSettings(
                        myLocationButtonEnabled = false,
                        mapToolbarEnabled = false,
                        zoomControlsEnabled = false,
                    ),
                contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
            ) {
                if (locationData != null) {
                    MarkerComposable(
                        state = MarkerState(position = position),
                        title = stringResource(id = R.string.location_tracker_current_location),
                        snippet = "${locationData.latitude}, ${locationData.longitude}",
                    ) {
                        UserLocationMarkerContent()
                    }
                }
            }
        }
        GoBackIconButton(
            modifier = Modifier.align(Alignment.TopStart),
            onGoBack = {
                onEvent(LocationTrackerUiEvent.StopTrackingRequested)
                onEvent(LocationTrackerUiEvent.GoBackRequested)
            },
        )
        if (state.isFineLocationPermissionGranted) {
            FollowUserIconButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                isFollowingUser = isFollowingUser,
                onClick = {
                    isFollowingUser = true
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(position, 15f)
                },
            )
        }
    }
}

@Composable
private fun UserLocationMarkerContent(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .size(16.dp)
                .background(
                    color = Color(0xFF4285F4),
                    shape = CircleShape,
                ).border(
                    width = 2.dp,
                    color = Color.White,
                    shape = CircleShape,
                ),
    )
}

@Composable
private fun GoBackIconButton(
    onGoBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier =
            modifier
                .padding(mediumPadding)
                .padding(WindowInsets.safeDrawing.asPaddingValues())
                .background(color = Color.White, shape = CircleShape),
        onClick = onGoBack,
    ) {
        Icon(
            painter = painterResource(id = com.alxnophis.jetpack.core.R.drawable.ic_arrow_back),
            contentDescription = stringResource(id = R.string.location_tracker_cd_go_back),
            tint = Color.Black,
        )
    }
}

@Composable
private fun FollowUserIconButton(
    isFollowingUser: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier =
            modifier
                .padding(mediumPadding)
                .padding(WindowInsets.safeDrawing.asPaddingValues())
                .background(
                    color = Color.White,
                    shape = CircleShape,
                ),
    ) {
        Icon(
            imageVector =
                if (isFollowingUser) {
                    Icons.Default.MyLocation
                } else {
                    Icons.Default.LocationSearching
                },
            contentDescription = stringResource(id = R.string.location_tracker_current_location),
            tint = if (isFollowingUser) MaterialTheme.colorScheme.primary else Color.Black,
        )
    }
}

@PreviewLightDark
@Composable
private fun LocationTrackerScreenPreview(
    @PreviewParameter(UserLocationPreviewProvider::class) uiState: LocationTrackerUiState,
) {
    LocationTrackerScreen(
        uiState = uiState,
        onEvent = {},
    )
}
