package com.alxnophis.jetpack.home.ui.contract

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.home.domain.model.NavigationItem

internal sealed class HomeEvent : UiEvent {
    data class ErrorDismissRequested(
        val errorId: Long = 0L,
    ) : HomeEvent()
}

@Immutable
internal data class HomeUiError(
    val id: Long = System.currentTimeMillis(),
    @param:StringRes val messageRes: Int,
)

@optics
@Immutable
internal data class HomeState(
    val isLoading: Boolean,
    val data: List<NavigationItem>,
    val error: HomeUiError?,
) : UiState {
    internal companion object {
        val initialState =
            HomeState(
                isLoading = false,
                data = emptyList(),
                error = null,
            )
    }
}
