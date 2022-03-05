package com.alxnophis.jetpack.settings.ui.view

import androidx.annotation.StringRes
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsTest : BaseComposeTest() {

    @Test
    fun settings_enable_notifications_is_displayed() {
        assertSettingsStringIsDisplayed(R.string.settings_option_notifications)
    }

    @Test
    fun settings_show_hints_is_displayed() {
        assertSettingsStringIsDisplayed(R.string.settings_option_hints)
    }

    @Test
    fun settings_subscription_is_displayed() {
        assertSettingsStringIsDisplayed(R.string.settings_option_manage_subscription)
    }

    @Test
    fun settings_theme_is_displayed() {
        assertSettingsStringIsDisplayed(R.string.settings_option_theme)
    }

    @Test
    fun settings_marketing_is_displayed() {
        assertSettingsStringIsDisplayed(R.string.settings_option_marketing)
    }

    @Test
    fun settings_version_is_displayed() {
        assertSettingsStringIsDisplayed(R.string.settings_app_version)
    }

    private fun assertSettingsStringIsDisplayed(
        @StringRes stringResource: Int,
    ) {
        assertStringIsDisplayedWith(stringResource) {
            SettingsScreen(
                settingsState = SettingsState(),
                appVersion = APP_VERSION,
                handleEvent = {}
            )
        }
    }

    companion object {
        private const val APP_VERSION = "1.0.0 (1)"
    }
}
