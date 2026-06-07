package com.alxnophis.jetpack.location.tracker.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiEvent
import com.alxnophis.jetpack.location.tracker.ui.viewmodel.LocationTrackerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LocationTrackerFeature(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel = koinViewModel<LocationTrackerViewModel>()
    LaunchedEffect(Unit) {
        if (context.hasLocationPermission()) {
            viewModel.handleEvent(LocationTrackerUiEvent.LocationPermissionGranted)
        }
    }
    LocationTrackerScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                is LocationTrackerUiEvent.GoBackRequested -> onBack()
                else -> viewModel.handleEvent(event)
            }
        },
    )
}
