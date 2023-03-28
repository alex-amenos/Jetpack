package com.alxnophis.jetpack.location.tracker.ui.view

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.core.ui.theme.smallPadding
import com.alxnophis.jetpack.location.tracker.R
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerState
import com.alxnophis.jetpack.location.tracker.ui.viewmodel.LocationTrackerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
internal fun LocationTrackerScreen(
    viewModel: LocationTrackerViewModel,
    popBackStack: () -> Unit
) {
    val state = viewModel.uiState.collectAsState().value
    BackHandler {
        viewModel
            .handleEvent(LocationTrackerEvent.EndTracking)
            .also { popBackStack() }
    }
    LocationTrackerContent(
        state = state,
        handleEvent = viewModel::handleEvent,
        navigateBack = popBackStack
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
internal fun LocationTrackerContent(
    state: LocationTrackerState,
    handleEvent: LocationTrackerEvent.() -> Unit,
    navigateBack: () -> Unit
) {
    AppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface),
            scaffoldState = rememberScaffoldState(),
            topBar = {
                CoreTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.location_tracker_title),
                    onBack = {
                        handleEvent(LocationTrackerEvent.EndTracking)
                        navigateBack()
                    }
                )
            }
        ) {
            LocationPermission(
                composableWithPermissionGranted = { UserLocation(state = state) },
                handleEvent = handleEvent
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun LocationPermission(
    composableWithPermissionGranted: @Composable () -> Unit,
    handleEvent: LocationTrackerEvent.() -> Unit
) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    if (locationPermissionsState.allPermissionsGranted) {
        handleEvent(LocationTrackerEvent.FineLocationPermissionGranted)
        composableWithPermissionGranted()
    } else {
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val allPermissionsRevoked = locationPermissionsState.permissions.size == locationPermissionsState.revokedPermissions.size
            val textIdToShow = if (!allPermissionsRevoked) {
                // If not all the permissions are revoked, it's because the user accepted the COARSE location permission, but not the FINE one.
                R.string.location_tracker_permissions_revoked
            } else if (locationPermissionsState.shouldShowRationale) {
                // Both location permissions have been denied
                R.string.location_tracker_location_permission_rationale
            } else {
                // First time the user sees this feature or the user doesn't want to be asked again
                R.string.location_tracker_feature_require_permission
            }
            val buttonPermissionsId = when {
                allPermissionsRevoked -> R.string.location_tracker_request_permissions
                else -> R.string.location_tracker_allow_fine_location
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(mediumPadding),
                textAlign = TextAlign.Justify,
                text = stringResource(textIdToShow)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                Text(text = stringResource(buttonPermissionsId))
            }
        }
    }
}

@Composable
private fun UserLocation(
    state: LocationTrackerState,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            modifier = Modifier.padding(start = mediumPadding, end = mediumPadding, top = mediumPadding, bottom = smallPadding),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.primary,
            fontSize = 16.sp,
            text = stringResource(id = R.string.location_tracker_last_known_location)
        )
        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(mediumPadding),
            text = state.lastKnownLocation ?: stringResource(id = R.string.location_tracker_location_not_available)
        )
        Text(
            modifier = Modifier.padding(start = mediumPadding, end = mediumPadding, top = mediumPadding, bottom = smallPadding),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.primary,
            fontSize = 16.sp,
            text = stringResource(id = R.string.location_tracker_current_location)
        )
        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(mediumPadding),
            text = state.userLocation ?: stringResource(id = R.string.location_tracker_location_not_available)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserLocationPreview() {
    val state = LocationTrackerState(
        lastKnownLocation = "Last known location",
        userLocation = "Current Location"
    )
    AppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            UserLocation(state)
        }
    }
}
