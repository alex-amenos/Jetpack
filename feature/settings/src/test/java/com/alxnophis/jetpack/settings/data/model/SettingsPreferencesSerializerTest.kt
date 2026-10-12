package com.alxnophis.jetpack.settings.data.model

import com.alxnophis.jetpack.testing.base.BaseUnitTest
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

internal class SettingsPreferencesSerializerTest : BaseUnitTest() {
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

            SettingsPreferencesSerializer.writeTo(original, output)
            val input = ByteArrayInputStream(output.toByteArray())
            val result = SettingsPreferencesSerializer.readFrom(input)

            result shouldBeEqualTo original
        }

    @Test
    fun `GIVEN empty input stream WHEN readFrom THEN return default value`() =
        runTest {
            val input = ByteArrayInputStream(ByteArray(0))
            val result = SettingsPreferencesSerializer.readFrom(input)

            result shouldBeEqualTo SettingsPreferences.default
        }
}
