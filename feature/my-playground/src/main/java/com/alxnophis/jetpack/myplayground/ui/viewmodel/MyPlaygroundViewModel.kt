package com.alxnophis.jetpack.myplayground.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.myplayground.ui.contract.MyPlaygroundEvent
import com.alxnophis.jetpack.myplayground.ui.contract.MyPlaygroundState
import kotlinx.coroutines.launch

internal class MyPlaygroundViewModel(
    initialState: MyPlaygroundState
) : BaseViewModel<MyPlaygroundEvent, MyPlaygroundState>(initialState) {

    override fun handleEvent(event: MyPlaygroundEvent) {
        viewModelScope.launch {
            when (event) {
                is MyPlaygroundEvent.TextFieldChanged -> updateTextField(event.value)
            }
        }
    }

    private fun updateTextField(value: String) {
        updateState {
            copy(textFieldValue = value)
        }
    }
}
