package com.alxnophis.jetpack.location.tracker.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.ui.composable.CoreButtonMajor
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.core.ui.theme.smallPadding
import com.alxnophis.jetpack.location.tracker.R
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
internal fun LocationTrackerScreen(
    state: LocationTrackerState,
    onEvent: (LocationTrackerEvent) -> Unit,
) {
    BackHandler {
        onEvent(LocationTrackerEvent.StopTrackingRequested)
        onEvent(LocationTrackerEvent.GoBackRequested)
    }
    AppTheme {
        Scaffold(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
            topBar = {
                CoreTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.location_tracker_title),
                    onBack = {
                        onEvent(LocationTrackerEvent.StopTrackingRequested)
                        onEvent(LocationTrackerEvent.GoBackRequested)
                    },
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { paddingValues ->
            LocationPermission(
                paddingValues = paddingValues,
                composableWithPermissionGranted = {
                    UserLocation(
                        paddingValues = paddingValues,
                        state = state,
                    )
                },
                handleEvent = onEvent,
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun LocationPermission(
    paddingValues: PaddingValues,
    composableWithPermissionGranted: @Composable () -> Unit,
    handleEvent: LocationTrackerEvent.() -> Unit,
) {
    val locationPermissionsState =
        rememberMultiplePermissionsState(
            listOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            ),
        )
    if (locationPermissionsState.allPermissionsGranted) {
        handleEvent(LocationTrackerEvent.FineLocationPermissionGranted)
        composableWithPermissionGranted()
    } else {
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .wrapContentSize()
                    .padding(mediumPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val allPermissionsRevoked = locationPermissionsState.permissions.size == locationPermissionsState.revokedPermissions.size
            val textIdToShow =
                if (!allPermissionsRevoked) {
                    // If not all the permissions are revoked, it's because the user accepted the COARSE location permission, but not the FINE one.
                    R.string.location_tracker_permissions_revoked
                } else if (locationPermissionsState.shouldShowRationale) {
                    // Both location permissions have been denied
                    R.string.location_tracker_location_permission_rationale
                } else {
                    // First time the user sees this feature or the user doesn't want to be asked again
                    R.string.location_tracker_feature_require_permission
                }
            val buttonPermissionsId =
                when {
                    allPermissionsRevoked -> R.string.location_tracker_request_permissions
                    else -> R.string.location_tracker_allow_fine_location
                }
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Justify,
                text = stringResource(textIdToShow),
            )
            Spacer(modifier = Modifier.height(mediumPadding + 25.dp))
            CoreButtonMajor(
                modifier = Modifier.fillMaxWidth(),
                onClick = { locationPermissionsState.launchMultiplePermissionRequest() },
                text = stringResource(buttonPermissionsId),
            )
        }
    }
}

@Composable
private fun UserLocation(
    paddingValues: PaddingValues,
    state: LocationTrackerState,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(paddingValues)) {
        Text(
            modifier = Modifier.padding(start = mediumPadding, end = mediumPadding, top = mediumPadding, bottom = smallPadding),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            text = stringResource(id = R.string.location_tracker_last_known_location),
        )
        Text(
            modifier =
                Modifier
                    .wrapContentSize()
                    .padding(mediumPadding),
            text =
                state.lastKnownLocation.ifEmpty {
                    stringResource(id = R.string.location_tracker_location_not_available)
                },
        )
        Text(
            modifier = Modifier.padding(start = mediumPadding, end = mediumPadding, top = mediumPadding, bottom = smallPadding),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            text = stringResource(id = R.string.location_tracker_current_location),
        )
        Text(
            modifier =
                Modifier
                    .wrapContentSize()
                    .padding(mediumPadding),
            text =
                state.userLocation.ifEmpty {
                    stringResource(id = R.string.location_tracker_location_not_available)
                },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserLocationPreview() {
    val state =
        LocationTrackerState(
            lastKnownLocation = "Last known location",
            userLocation = "Current Location",
        )
    AppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            UserLocation(
                paddingValues = PaddingValues(),
                state = state,
            )
        }
    }
}
