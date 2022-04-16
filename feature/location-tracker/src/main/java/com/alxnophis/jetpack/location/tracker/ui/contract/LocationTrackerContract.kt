package com.alxnophis.jetpack.location.tracker.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEffect
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState

internal sealed class LocationTrackerEffect : UiEffect {
    object Finish : LocationTrackerEffect()
}

internal sealed class LocationTrackerEvent : UiEvent {
    object Finish : LocationTrackerEvent()
}

internal data class LocationTrackerState(
    val currentLatitude: Double = 0.0,
    val currentLongitude: Double = 0.0,
) : UiState
