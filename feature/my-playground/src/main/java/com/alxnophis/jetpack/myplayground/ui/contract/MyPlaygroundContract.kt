package com.alxnophis.jetpack.myplayground.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.kotlin.constants.EMPTY

internal sealed class MyPlaygroundEvent : UiEvent {
    data class TextFieldChanged(val value: String) : MyPlaygroundEvent()
}

internal data class MyPlaygroundState(
    val textFieldValue: String = EMPTY
) : UiState
