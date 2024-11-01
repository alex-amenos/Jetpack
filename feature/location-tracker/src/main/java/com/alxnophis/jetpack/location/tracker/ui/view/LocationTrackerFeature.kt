package com.alxnophis.jetpack.location.tracker.ui.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.location.tracker.di.injectLocationTracker
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEvent
import com.alxnophis.jetpack.location.tracker.ui.viewmodel.LocationTrackerViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun LocationTrackerFeature(
    onBack: () -> Unit,
) {
    injectLocationTracker()
    val viewModel = getViewModel<LocationTrackerViewModel>()
    LocationTrackerScreen(
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                is LocationTrackerEvent.GoBackRequested -> onBack()
                else -> viewModel.handleEvent(event)
            }
        },
    )
}
