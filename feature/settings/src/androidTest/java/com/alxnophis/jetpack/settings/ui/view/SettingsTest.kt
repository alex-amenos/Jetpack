package com.alxnophis.jetpack.settings.ui.view

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.alxnophis.jetpack.settings.R
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.view.SettingsTags.TAG_CHECK_ITEM
import com.alxnophis.jetpack.settings.ui.view.SettingsTags.TAG_MARKETING_OPTION
import com.alxnophis.jetpack.settings.ui.view.SettingsTags.TAG_TOGGLE_ITEM
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test

class SettingsTest : BaseComposeTest() {

    @Test
    fun settings_enable_notifications_is_displayed() {
        setSettingsContent()
        assertStringResDisplayed(R.string.settings_option_notifications)
    }

    @Test
    fun settings_show_hints_is_displayed() {
        setSettingsContent()
        assertStringResDisplayed(R.string.settings_option_hints)
    }

    @Test
    fun settings_subscription_is_displayed() {
        setSettingsContent()
        assertStringResDisplayed(R.string.settings_option_manage_subscription)
    }

    @Test
    fun settings_theme_is_displayed() {
        setSettingsContent()
        assertStringResDisplayed(R.string.settings_option_theme)
    }

    @Test
    fun settings_marketing_is_displayed() {
        setSettingsContent()
        assertStringResDisplayed(R.string.settings_option_marketing)
    }

    @Test
    fun settings_version_is_displayed() {
        setSettingsContent()
        assertStringResDisplayed(R.string.settings_app_version)
    }

    @Test
    fun enabled_notifications_toggles_state() {
        setSettingsContent()
        composeTestRule
            .onNodeWithTag(TAG_TOGGLE_ITEM)
            .assertIsOff()
        composeTestRule
            .onNodeWithText(context.getString(R.string.settings_option_notifications))
            .performClick()
        composeTestRule
            .onNodeWithTag(TAG_TOGGLE_ITEM)
            .assertIsOn()
    }

    @Test
    fun show_hints_toggles_state() {
        setSettingsContent()
        composeTestRule
            .onNodeWithTag(TAG_CHECK_ITEM)
            .assertIsOff()
        composeTestRule
            .onNodeWithText(context.getString(R.string.settings_option_hints))
            .performClick()
        composeTestRule
            .onNodeWithTag(TAG_CHECK_ITEM)
            .assertIsOn()
    }

    @Test
    fun marketing_options_toggles_state() {
        setSettingsContent()
        composeTestRule
            .onNodeWithTag(TAG_MARKETING_OPTION + MarketingOption.ALLOWED.id)
            .assertIsSelected()
        composeTestRule
            .onNodeWithText(context.resources.getStringArray(R.array.settings_options_marketing_choice)[1])
            .performClick()
        composeTestRule
            .onNodeWithTag(TAG_MARKETING_OPTION + MarketingOption.NOT_ALLOWED.id)
            .assertIsSelected()
    }

    private fun assertStringResDisplayed(@StringRes stringResource: Int) {
        composeTestRule
            .onNodeWithText(targetContext.getString(stringResource))
            .assertIsDisplayed()
    }

    private fun setSettingsContent() {
        composeTestRule.setContent {
            SettingsScreen(
                navController = rememberNavController(),
                viewModel = SettingsViewModel(initialState = SettingsState()),
                appVersion = APP_VERSION
            )
        }
    }

    companion object {
        private const val APP_VERSION = "1.0.0 (1)"
    }
}
