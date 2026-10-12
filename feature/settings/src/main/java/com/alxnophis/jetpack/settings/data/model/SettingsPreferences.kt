package com.alxnophis.jetpack.settings.data.model

import androidx.annotation.StringRes
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.alxnophis.jetpack.core.base.crypto.Crypto
import com.alxnophis.jetpack.settings.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64
import java.util.concurrent.atomic.AtomicBoolean

@Serializable
data class SettingsPreferences(
    val notificationsEnabled: Boolean,
    val hintsEnabled: Boolean,
    val marketingOption: Boolean,
    val themeOption: ThemeOptions,
) {
    @Serializable
    enum class ThemeOptions(
        @StringRes val label: Int,
    ) {
        LIGHT(R.string.settings_light),
        DARK(R.string.settings_dark),
        SYSTEM(R.string.settings_system),
    }

    companion object {
        val default =
            SettingsPreferences(
                notificationsEnabled = false,
                hintsEnabled = false,
                marketingOption = true,
                themeOption = ThemeOptions.SYSTEM,
            )
    }
}

/**
 * Serializer for [SettingsPreferences].
 *
 * Persists settings as UTF-8 plaintext JSON for cold startup performance.
 * On first upgrade from legacy encrypted versions, it decrypts using [Crypto]
 * and flags [hasLegacyEncryptedData] so DataStore migrations rewrite plaintext atomically.
 */
internal class SettingsPreferencesSerializer(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : Serializer<SettingsPreferences> {
    override val defaultValue: SettingsPreferences
        get() = SettingsPreferences.default

    val hasLegacyEncryptedData = AtomicBoolean(false)

    internal var decryptPayload: (ByteArray) -> ByteArray = { Crypto.decrypt(it) }

    override suspend fun readFrom(input: InputStream): SettingsPreferences {
        val text =
            withContext(ioDispatcher) {
                input
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }
            }
        if (text.isBlank()) return defaultValue
        return try {
            Json.decodeFromString(text)
        } catch (jsonException: Exception) {
            try {
                val encryptedBytesDecoded =
                    Base64
                        .getDecoder()
                        .decode(text)
                val decryptedBytes = decryptPayload(encryptedBytesDecoded)
                val preferences = Json.decodeFromString<SettingsPreferences>(decryptedBytes.decodeToString())
                hasLegacyEncryptedData.set(true)
                preferences
            } catch (cryptoException: Exception) {
                throw CorruptionException("Failed to parse or decrypt SettingsPreferences payload", jsonException)
            }
        }
    }

    override suspend fun writeTo(
        t: SettingsPreferences,
        output: OutputStream,
    ) {
        val json = Json.encodeToString(t)
        withContext(ioDispatcher) {
            output.use {
                it.write(json.toByteArray(Charsets.UTF_8))
            }
        }
    }
}
