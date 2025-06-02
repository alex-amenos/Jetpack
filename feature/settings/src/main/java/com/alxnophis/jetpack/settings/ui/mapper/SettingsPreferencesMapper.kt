package com.alxnophis.jetpack.settings.ui.mapper

import com.alxnophis.jetpack.settings.data.model.SettingsPreferences
import com.alxnophis.jetpack.settings.ui.contract.Theme

internal fun SettingsPreferences.ThemeOptions.map(): Theme =
    when (this) {
        SettingsPreferences.ThemeOptions.LIGHT -> Theme.LIGHT
        SettingsPreferences.ThemeOptions.DARK -> Theme.DARK
        SettingsPreferences.ThemeOptions.SYSTEM -> Theme.SYSTEM
    }
