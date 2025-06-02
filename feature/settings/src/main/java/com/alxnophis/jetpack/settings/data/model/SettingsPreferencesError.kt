package com.alxnophis.jetpack.settings.data.model

internal sealed class SettingsPreferencesError {
    data object Disk : SettingsPreferencesError()

    data object Unexpected : SettingsPreferencesError()
}
