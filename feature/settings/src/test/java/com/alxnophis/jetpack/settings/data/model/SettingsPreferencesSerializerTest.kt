package com.alxnophis.jetpack.settings.data.model

import androidx.datastore.core.CorruptionException
import com.alxnophis.jetpack.core.base.crypto.Crypto
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64

internal class SettingsPreferencesSerializerTest : BaseUnitTest() {
    private val serializer = SettingsPreferencesSerializer(ioDispatcher = testDispatcher)

    @AfterEach
    fun tearDown() {
        serializer.hasLegacyEncryptedData.set(false)
        serializer.decryptPayload = { Crypto.decrypt(it) }
    }

    @Test
    fun `GIVEN settings preferences WHEN writeTo and readFrom THEN preferences are preserved`() =
        runTest {
            val original =
                SettingsPreferencesMother.create(
                    notificationsEnabled = true,
                    hintsEnabled = true,
                    marketingOption = false,
                    themeOption = SettingsPreferences.ThemeOptions.DARK,
                )
            val output = ByteArrayOutputStream()

            serializer.writeTo(original, output)
            val input = ByteArrayInputStream(output.toByteArray())
            val result = serializer.readFrom(input)

            result shouldBeEqualTo original
            serializer.hasLegacyEncryptedData.get() shouldBeEqualTo false
        }

    @Test
    fun `GIVEN empty input stream WHEN readFrom THEN return default value`() =
        runTest {
            val input = ByteArrayInputStream(ByteArray(0))
            val result = serializer.readFrom(input)

            result shouldBeEqualTo SettingsPreferences.default
            serializer.hasLegacyEncryptedData.get() shouldBeEqualTo false
        }

    @Test
    fun `GIVEN legacy encrypted payload WHEN readFrom THEN decrypts successfully and marks migration needed`() =
        runTest {
            val expected =
                SettingsPreferencesMother.create(
                    notificationsEnabled = true,
                    hintsEnabled = false,
                    marketingOption = true,
                    themeOption = SettingsPreferences.ThemeOptions.LIGHT,
                )
            val expectedJsonBytes =
                Json
                    .encodeToString(expected)
                    .toByteArray(Charsets.UTF_8)
            val fakeCipherBytes = byteArrayOf(1, 2, 3, 4, 5)
            val legacyBase64Payload =
                Base64
                    .getEncoder()
                    .encodeToString(fakeCipherBytes)

            serializer.decryptPayload = { bytes ->
                bytes shouldBeEqualTo fakeCipherBytes
                expectedJsonBytes
            }

            val input = ByteArrayInputStream(legacyBase64Payload.toByteArray(Charsets.UTF_8))
            val result = serializer.readFrom(input)

            result shouldBeEqualTo expected
            serializer.hasLegacyEncryptedData.get() shouldBeEqualTo true
        }

    @Test
    fun `GIVEN corrupt input stream WHEN readFrom THEN throws CorruptionException`() =
        runTest {
            serializer.decryptPayload = {
                throw IllegalStateException("Decryption failed")
            }
            val corruptInput = ByteArrayInputStream("not-valid-json-and-not-base64-payload!!!".toByteArray(Charsets.UTF_8))

            assertThrows<CorruptionException> {
                serializer.readFrom(corruptInput)
            }
        }
}
