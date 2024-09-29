package com.alxnophis.jetpack.settings.ui.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test

class SettingsNotificationItemTest : BaseComposeTest() {
    @Test
    fun title_displayed() {
        val title = "Enable Notifications"
        composeTestRule.setContent {
            SettingsNotificationItem(
                title = title,
                checked = true,
                onToggleNotificationSettings = {},
            )
        }
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()
    }

    @Test
    fun settings_checked() {
        composeTestRule.setContent {
            SettingsNotificationItem(
                title = "Enable Notifications",
                checked = true,
                onToggleNotificationSettings = {},
            )
        }
        composeTestRule
            .onNodeWithTag(SettingsTags.TAG_TOGGLE_ITEM)
            .assertIsOn()
    }
}
