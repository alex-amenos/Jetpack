package com.alxnophis.jetpack.settings.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.dataStore
import arrow.core.Either
import arrow.core.Either.Companion.catch
import com.alxnophis.jetpack.settings.data.model.SettingsPreferences
import com.alxnophis.jetpack.settings.data.model.SettingsPreferencesError
import com.alxnophis.jetpack.settings.data.model.SettingsPreferencesSerializer
import kotlinx.coroutines.flow.Flow

internal typealias UpdatedSuccessfully = Unit

internal class SettingsRepositoryImpl(
    private val context: Context,
) : SettingsRepository {
    private val Context.dataStore: DataStore<SettingsPreferences> by dataStore(
        fileName = SettingsPreferences.FILE_NAME,
        serializer = SettingsPreferencesSerializer,
    )

    private val settingsDataStore: DataStore<SettingsPreferences>
        get() = context.dataStore

    private val settingsDataStoreData: Flow<SettingsPreferences>
        get() = context.dataStore.data

    override fun getSettingsFlow(): Flow<SettingsPreferences> = settingsDataStoreData

    private suspend inline fun updatePreference(crossinline update: (SettingsPreferences) -> SettingsPreferences): Either<SettingsPreferencesError, Unit> =
        catch {
            settingsDataStore.updateData { update(it) }
        }.map { UpdatedSuccessfully }
            .mapLeft { error ->
                when (error) {
                    is IOException -> SettingsPreferencesError.Disk
                    else -> SettingsPreferencesError.Unexpected
                }
            }

    override suspend fun updateNotificationsEnabled(enabled: Boolean): Either<SettingsPreferencesError, Unit> = updatePreference { it.copy(notificationsEnabled = enabled) }

    override suspend fun updateHintsEnabled(enabled: Boolean): Either<SettingsPreferencesError, Unit> = updatePreference { it.copy(hintsEnabled = enabled) }

    override suspend fun updateMarketingOption(enabled: Boolean): Either<SettingsPreferencesError, Unit> = updatePreference { it.copy(marketingOption = enabled) }

    override suspend fun updateThemeOption(themeOption: SettingsPreferences.ThemeOptions): Either<SettingsPreferencesError, Unit> = updatePreference { it.copy(themeOption = themeOption) }
}
