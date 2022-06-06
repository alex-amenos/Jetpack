package com.alxnophis.jetpack.settings.ui.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test

class SettingsAppVersionTest : BaseComposeTest() {

    @Test
    fun app_version_displayed() {
        val version = "1.0.0"
        composeTestRule.setContent {
            SettingsAppVersion(appVersion = version)
        }
        composeTestRule
            .onNodeWithText(version)
            .assertIsDisplayed()
    }
}
