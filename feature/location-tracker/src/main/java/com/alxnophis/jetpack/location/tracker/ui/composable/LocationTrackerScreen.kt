package com.alxnophis.jetpack.location.tracker.ui.composable

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.core.ui.composable.CoreButtonMajor
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.largePadding
import com.alxnophis.jetpack.core.ui.theme.mediumPadding
import com.alxnophis.jetpack.core.ui.theme.smallPadding
import com.alxnophis.jetpack.location.tracker.R
import com.alxnophis.jetpack.location.tracker.ui.composable.provider.UserLocationPreviewProvider
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiState

@Composable
internal fun LocationTrackerScreen(
    uiState: LocationTrackerUiState,
    onEvent: (LocationTrackerUiEvent) -> Unit,
) {
    BackHandler {
        onEvent(LocationTrackerUiEvent.StopTrackingRequested)
        onEvent(LocationTrackerUiEvent.GoBackRequested)
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
                        onEvent(LocationTrackerUiEvent.StopTrackingRequested)
                        onEvent(LocationTrackerUiEvent.GoBackRequested)
                    },
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { paddingValues ->
            if (uiState.isFineLocationPermissionGranted) {
                UserLocation(
                    state = uiState,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                )
            } else {
                LocationPermission(
                    handleEvent = onEvent,
                    modifier =
                        Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .padding(mediumPadding),
                )
            }
        }
    }
}

@Composable
private fun LocationPermission(
    handleEvent: LocationTrackerUiEvent.() -> Unit,
    modifier: Modifier = Modifier,
) {
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions: Map<String, Boolean> ->
                if (permissions.values.all { it }) {
                    LocationTrackerUiEvent.FineLocationPermissionGrantedUi.handleEvent()
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Justify,
            text = stringResource(R.string.location_tracker_feature_require_permission),
        )
        Spacer(modifier = Modifier.height(mediumPadding + 25.dp))
        CoreButtonMajor(
            modifier = Modifier.fillMaxWidth(),
            onClick = { requestLocationPermissions() },
            text = stringResource(R.string.location_tracker_allow_fine_location),
        )
    }
}

@Composable
private fun UserLocation(
    state: LocationTrackerUiState,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
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
                    .padding(largePadding),
            text =
                state
                    .lastKnownLocation
                    .ifEmpty { stringResource(id = R.string.location_tracker_location_not_available) },
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
                state
                    .userLocation
                    .ifEmpty { stringResource(id = R.string.location_tracker_location_not_available) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserLocationPreview(
    @PreviewParameter(UserLocationPreviewProvider::class) uiState: LocationTrackerUiState,
) {
    LocationTrackerScreen(
        uiState = uiState,
        onEvent = {},
    )
}
