package com.alxnophis.jetpack.location.tracker.ui.view

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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.location.tracker.R
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEffect
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerState
import com.alxnophis.jetpack.location.tracker.ui.viewmodel.LocationTrackerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.getViewModel

@Composable
internal fun LocationTrackerScreen(
    navController: NavController,
    viewModel: LocationTrackerViewModel = getViewModel(),
) {
    CoreTheme {
        val state = viewModel.uiState.collectAsState().value
        LocationTracker(
            state = state,
            onLocationTrackerEvent = viewModel::setEvent,
        )
        BackHandler {
            viewModel.setEvent(LocationTrackerEvent.NavigateBack)
        }
        LaunchedEffect(key1 = Unit) {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    LocationTrackerEffect.NavigateBack -> navController.popBackStack()
                }
            }
        }
    }
}

@Composable
internal fun LocationTracker(
    state: LocationTrackerState,
    onLocationTrackerEvent: (event: LocationTrackerEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        CoreTopBar(
            title = stringResource(id = R.string.location_tracker_title),
            onBack = { onLocationTrackerEvent(LocationTrackerEvent.NavigateBack) },
        )
        LocationPermission {
            UserLocation(
                state = state
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun LocationPermission(
    composableWhenPermissionGranted: @Composable () -> Unit,
) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )
    if (locationPermissionsState.allPermissionsGranted) {
        // TODO - Check if GPS is enabled and show a dialog if not
        composableWhenPermissionGranted()
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
                    .padding(16.dp),
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
) {
    Text(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        text = stringResource(id = R.string.location_tracker_current_location)
    )
    Text(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        text = state.userLocation.toString()
    )
}
