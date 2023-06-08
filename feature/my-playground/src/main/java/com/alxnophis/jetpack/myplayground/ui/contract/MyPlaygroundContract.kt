package com.alxnophis.jetpack.myplayground.ui.contract

import arrow.optics.optics
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.kotlin.constants.EMPTY

internal sealed class MyPlaygroundEvent : UiEvent {
    data class TextFieldChanged(val value: String) : MyPlaygroundEvent()
}

@optics
internal data class MyPlaygroundState(
    val textFieldValue: String
) : UiState {
    internal companion object {
        val initialState = MyPlaygroundState(
            textFieldValue = EMPTY
        )
    }
}
