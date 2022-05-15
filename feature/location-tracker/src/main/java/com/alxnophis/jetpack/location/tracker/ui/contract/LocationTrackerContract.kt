package com.alxnophis.jetpack.location.tracker.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEffect
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.location.tracker.domain.model.Location

internal sealed class LocationTrackerEffect : UiEffect {
    object NavigateBack : LocationTrackerEffect()
}

internal sealed class LocationTrackerEvent : UiEvent {
    object NavigateBack : LocationTrackerEvent()
}

internal data class LocationTrackerState(
    val userLocation: Location? = null,
) : UiState
