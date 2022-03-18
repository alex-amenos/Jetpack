package com.alxnophis.jetpack.settings.ui.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.alxnophis.jetpack.settings.ui.contract.Theme
import com.alxnophis.jetpack.settings.ui.view.SettingsTags.TAG_SELECT_THEME
import com.alxnophis.jetpack.settings.ui.view.SettingsTags.TAG_THEME_OPTION
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test

class SettingsThemeItemTest : BaseComposeTest() {

    @Test
    fun selected_theme_displayed() {
        val option = Theme.DARK
        composeTestRule.setContent {
            SettingsThemeItem(
                selectedTheme = option,
                onOptionSelected = {}
            )
        }
        composeTestRule
            .onNodeWithTag(SettingsTags.TAG_THEME, useUnmergedTree = true)
            .assertTextEquals(targetContext.getString(option.label))
    }

    @Test
    fun theme_options_displayed() {
        composeTestRule.setContent {
            SettingsThemeItem(
                selectedTheme = Theme.DARK,
                onOptionSelected = { }
            )
        }
        composeTestRule
            .onNodeWithTag(TAG_SELECT_THEME)
            .performClick()
        Theme.values().forEach {
            composeTestRule
                .onNodeWithTag(TAG_THEME_OPTION + it)
                .assertIsDisplayed()
        }
    }
}
