package com.alxnophis.jetpack.settings.data.model

import com.alxnophis.jetpack.settings.data.model.SettingsPreferences.ThemeOptions

internal object SettingsPreferencesMother {
    fun create(
        notificationsEnabled: Boolean = false,
        hintsEnabled: Boolean = false,
        marketingOption: Boolean = false,
        themeOption: ThemeOptions = ThemeOptions.SYSTEM,
    ) = SettingsPreferences(
        notificationsEnabled = notificationsEnabled,
        hintsEnabled = hintsEnabled,
        marketingOption = marketingOption,
        themeOption = themeOption,
    )
}
