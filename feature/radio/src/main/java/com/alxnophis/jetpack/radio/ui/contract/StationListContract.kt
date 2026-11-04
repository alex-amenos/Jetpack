package com.alxnophis.jetpack.radio.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.radio.domain.model.Station
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal sealed interface StationListEvent : UiEvent {
    data object Retry : StationListEvent

    data class Select(
        val index: Int,
    ) : StationListEvent
}

@Immutable
internal enum class ListStatus { Loading, Content, Empty, Error }

@optics
@Immutable
internal data class StationListUiState(
    val status: ListStatus,
    val stations: ImmutableList<Station>,
) : UiState {
    internal companion object {
        val initialState =
            StationListUiState(
                status = ListStatus.Loading,
                stations = persistentListOf(),
            )
    }
}
