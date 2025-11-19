package com.alxnophis.jetpack.myplayground.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.kotlin.constants.EMPTY

internal sealed class MyPlaygroundEvent : UiEvent {
    data object GoBackRequested : MyPlaygroundEvent()

    data class TextFieldChanged(
        val value: String,
    ) : MyPlaygroundEvent()
}

@optics
@Immutable
internal data class MyPlaygroundState(
    val textFieldValue: String,
) : UiState {
    internal companion object {
        val initialState =
            MyPlaygroundState(
                textFieldValue = EMPTY,
            )
    }
}
