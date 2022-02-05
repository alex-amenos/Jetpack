package com.alxnophis.jetpack.settings.ui.view

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsTest : BaseComposeTest() {

    @Test
    fun enable_notifications_settings_is_displayed() {
        assertSettingIsDisplayedWithState(
            R.string.settings_option_notifications
        ) {
            composeTestRule.setContent {
                SettingsScreen(
                    settingsState = SettingsState().copy(notificationsEnabled = true),
                    appVersion = APP_VERSION,
                    handleEvent = {}
                )
            }
        }
    }

    private fun assertSettingIsDisplayedWithState(
        @StringRes title: Int,
        content: () -> Unit
    ) {
        content()
        composeTestRule
            .onNodeWithText(targetContext.getString(title))
            .assertIsDisplayed()
    }

    companion object {
        private const val APP_VERSION = "1.0.0 (1)"
    }
}
