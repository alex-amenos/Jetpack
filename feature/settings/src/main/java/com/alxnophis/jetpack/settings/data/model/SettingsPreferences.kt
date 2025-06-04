package com.alxnophis.jetpack.settings.data.model

import androidx.annotation.StringRes
import androidx.datastore.core.Serializer
import com.alxnophis.jetpack.core.base.crypto.Crypto
import com.alxnophis.jetpack.settings.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

@Serializable
internal data class SettingsPreferences(
    val notificationsEnabled: Boolean,
    val hintsEnabled: Boolean,
    val marketingOption: Boolean,
    val themeOption: ThemeOptions,
) {
    @Serializable
    internal enum class ThemeOptions(
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
 * This serializer handles the reading and writing of [SettingsPreferences] to and from an [InputStream]
 * and [OutputStream] respectively. The serialization process involves the following steps for security:
 *
 * 1. **Serialization to JSON**: The [SettingsPreferences] object is first converted into a JSON string.
 * 2. **Encryption**: The JSON string (as bytes) is then encrypted using [Crypto].
 * 3. **Base64 Encoding**: The encrypted bytes are Base64 encoded to ensure safe transmission and storage as a string.
 *
 * The deserialization process reverses these steps:
 *
 * 1. **Base64 Decoding**: The input string is Base64 decoded.
 * 2. **Decryption**: The resulting bytes are decrypted using [Crypto].
 * 3. **Deserialization from JSON**: The decrypted string is parsed from JSON back into a [SettingsPreferences] object.
 *
 * Encryption is applied to protect the user's settings data.
 */
internal object SettingsPreferencesSerializer : Serializer<SettingsPreferences> {
    override val defaultValue: SettingsPreferences
        get() = SettingsPreferences.default

    override suspend fun readFrom(input: InputStream): SettingsPreferences {
        val encryptedBytes =
            withContext(Dispatchers.IO) {
                input.bufferedReader().use { it.readText() }
            }
        val encryptedBytesDecoded = Base64.getDecoder().decode(encryptedBytes)
        val decryptedBytes = Crypto.decrypt(encryptedBytesDecoded)
        val decodeJsonString = decryptedBytes.decodeToString()
        return Json.decodeFromString(decodeJsonString)
    }

    override suspend fun writeTo(
        t: SettingsPreferences,
        output: OutputStream,
    ) {
        val json = Json.encodeToString(t)
        val bytes = json.toByteArray()
        val encryptedBytes = Crypto.encrypt(bytes)
        val encryptedBytesBase64 = Base64.getEncoder().encode(encryptedBytes)
        withContext(Dispatchers.IO) {
            output.use {
                it.write(encryptedBytesBase64)
            }
        }
    }
}
