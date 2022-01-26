package com.alxnophis.jetpack.settings.ui.viewmodel

import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.settings.ui.contract.SettingsEffect
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsState

internal class SettingsViewModel(
    initialState: SettingsState = SettingsState(),
): BaseViewModel<SettingsEvent, SettingsState, SettingsEffect>(initialState){

    override fun handleEvent(event: SettingsEvent) {
        TODO("Not yet implemented")
    }
}
