package com.alxnophis.jetpack.myplayground.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.myplayground.ui.contract.MyPlaygroundEvent
import com.alxnophis.jetpack.myplayground.ui.contract.MyPlaygroundState
import com.alxnophis.jetpack.myplayground.ui.contract.textFieldValue
import kotlinx.coroutines.launch

internal class MyPlaygroundViewModel(
    initialState: MyPlaygroundState = MyPlaygroundState.initialState
) : BaseViewModel<MyPlaygroundEvent, MyPlaygroundState>(initialState) {

    override fun handleEvent(event: MyPlaygroundEvent) {
        viewModelScope.launch {
            when (event) {
                MyPlaygroundEvent.GoBackRequested -> throw IllegalStateException("GoBackRequested not implemented")
                is MyPlaygroundEvent.TextFieldChanged -> updateUiState {
                    copy {
                        MyPlaygroundState.textFieldValue set event.value
                    }
                }
            }
        }
    }
}
