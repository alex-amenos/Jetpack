package com.alxnophis.jetpack.location.tracker.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerUiEvent
import com.alxnophis.jetpack.location.tracker.ui.viewmodel.LocationTrackerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LocationTrackerFeature(onBack: () -> Unit) {
    val viewModel = koinViewModel<LocationTrackerViewModel>()
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
