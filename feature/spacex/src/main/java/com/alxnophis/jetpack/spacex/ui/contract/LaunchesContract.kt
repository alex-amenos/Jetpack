package com.alxnophis.jetpack.spacex.ui.contract

import arrow.optics.optics
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchModel

internal sealed class LaunchesEvent : UiEvent {
    object Initialize : LaunchesEvent()
    object RefreshPastLaunches : LaunchesEvent()
    data class DismissError(val errorId: Long) : LaunchesEvent()
}

@optics
internal data class LaunchesState(
    val isLoading: Boolean,
    val pastLaunches: List<PastLaunchModel>,
    val errorMessages: List<ErrorMessage>
) : UiState {
    internal companion object {
        val initialState = LaunchesState(
            isLoading = false,
            pastLaunches = emptyList(),
            errorMessages = emptyList()
        )
    }
}
