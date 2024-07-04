package com.alxnophis.jetpack.spacex.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchModel

internal sealed class LaunchesEvent : UiEvent {
    object Initialized : LaunchesEvent()
    object RefreshPastLaunchesRequested : LaunchesEvent()
    object GoBackRequested : LaunchesEvent()
    data class DismissErrorRequested(val errorId: Long) : LaunchesEvent()
}

@Immutable
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
