package com.alxnophis.jetpack.settings.ui.composable

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test

internal class SettingsAppVersionTest : BaseComposeTest() {
    @Test
    fun app_version_displayed() {
        composeTestRule.setContent {
            SettingsAppVersion(appVersion = VERSION)
        }
        composeTestRule
            .onNodeWithText(VERSION)
            .assertIsDisplayed()
    }

    companion object {
        private const val VERSION = "1.0.0"
    }
}
