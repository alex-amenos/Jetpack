package com.alxnophis.jetpack.radio.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.radio.data.model.RadioStation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal sealed interface RadioStationListEvent : UiEvent {
    data object Retry : RadioStationListEvent

    data class Select(
        val index: Int,
    ) : RadioStationListEvent
}

@Immutable
internal enum class RadioStationListStatus {
    Loading,
    Content,
    Empty,
    Error,
}

@optics
@Immutable
internal data class RadioStationListUiState(
    val status: RadioStationListStatus,
    val stations: ImmutableList<RadioStation>,
    val currentStationId: String? = null,
) : UiState {
    internal companion object {
        val initialState =
            RadioStationListUiState(
                status = RadioStationListStatus.Loading,
                stations = persistentListOf(),
            )
    }
}
