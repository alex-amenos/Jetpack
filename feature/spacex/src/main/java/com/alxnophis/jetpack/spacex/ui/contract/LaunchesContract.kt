package com.alxnophis.jetpack.spacex.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchModel

internal sealed class LaunchesEvent : UiEvent {
    object GetPastLaunches : LaunchesEvent()
    object RefreshPastLaunches : LaunchesEvent()
    data class DismissError(val errorId: Long) : LaunchesEvent()
}

internal data class LaunchesState(
    val isLoading: Boolean = false,
    val pastLaunches: List<PastLaunchModel> = emptyList(),
    val errorMessages: List<ErrorMessage> = emptyList(),
) : UiState
