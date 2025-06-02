package com.alxnophis.jetpack.settings.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.settings.data.model.SettingsPreferences
import com.alxnophis.jetpack.settings.data.model.SettingsPreferencesError

internal interface SettingsRepository {
    suspend fun getSettings(): SettingsPreferences

    suspend fun updateNotificationsEnabled(enabled: Boolean): Either<SettingsPreferencesError, Unit>

    suspend fun updateHintsEnabled(enabled: Boolean): Either<SettingsPreferencesError, Unit>

    suspend fun updateMarketingOption(enabled: Boolean): Either<SettingsPreferencesError, Unit>

    suspend fun updateThemeOption(themeOption: SettingsPreferences.ThemeOptions): Either<SettingsPreferencesError, Unit>
}
