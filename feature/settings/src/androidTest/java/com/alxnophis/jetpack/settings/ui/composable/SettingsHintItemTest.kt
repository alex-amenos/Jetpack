package com.alxnophis.jetpack.settings.ui.composable

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test

class SettingsHintItemTest : BaseComposeTest() {
    @Test
    fun title_displayed() {
        val title = "Show Hints"
        composeTestRule.setContent {
            SettingsHintItem(
                title = title,
                checked = true,
                onShowHintToggled = {},
            )
        }
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()
    }

    @Test
    fun settings_checked() {
        composeTestRule.setContent {
            SettingsHintItem(
                title = "Show Hints",
                checked = true,
                onShowHintToggled = {},
            )
        }
        composeTestRule
            .onNodeWithTag(SettingsTags.TAG_CHECK_ITEM)
            .assertIsOn()
    }
}
