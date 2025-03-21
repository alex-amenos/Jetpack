package com.alxnophis.jetpack.settings.ui.composable

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test

internal class SettingsMarketingItemTest : BaseComposeTest() {
    @Test
    fun marketing_option_selected() {
        val option = MarketingOption.NOT_ALLOWED
        composeTestRule.setContent {
            SettingsMarketingItem(
                selectedOption = option,
                onOptionSelected = { },
            )
        }
        composeTestRule
            .onNodeWithTag(SettingsTags.TAG_MARKETING_OPTION + option.id)
            .assertIsSelected()
    }
}
