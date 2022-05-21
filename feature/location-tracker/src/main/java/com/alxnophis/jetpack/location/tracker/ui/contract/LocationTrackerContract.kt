package com.alxnophis.jetpack.location.tracker.ui.contract

import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.base.viewmodel.UiEffect
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState

internal sealed class LocationTrackerEffect : UiEffect {
    object NavigateBack : LocationTrackerEffect()
}

internal sealed class LocationTrackerEvent : UiEvent {
    object FineLocationPermissionGranted : LocationTrackerEvent()
    object NavigateBack : LocationTrackerEvent()
}

internal data class LocationTrackerState(
    val userLocation: String? = EMPTY,
) : UiState
