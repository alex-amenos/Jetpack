package com.alxnophis.jetpack.root.ui.contract

import androidx.compose.runtime.Immutable
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.settings.data.model.SettingsPreferences

internal sealed interface RootUiEvent : UiEvent

@Immutable
internal data class RootUiState(
    val themeOption: SettingsPreferences.ThemeOptions,
    val isOnline: Boolean,
) : UiState {
    companion object {
        val initialState =
            RootUiState(
                themeOption = SettingsPreferences.ThemeOptions.SYSTEM,
                isOnline = true,
            )
    }
}
