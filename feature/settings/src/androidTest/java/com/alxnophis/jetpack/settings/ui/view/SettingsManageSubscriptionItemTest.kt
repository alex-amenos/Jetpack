package com.alxnophis.jetpack.settings.ui.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class SettingsManageSubscriptionItemTest : BaseComposeTest() {
    @Test
    fun title_displayed() {
        val title = "Manage Subscription"
        composeTestRule.setContent {
            SettingsManageSubscriptionItem(
                title = title,
                onSubscriptionClicked = {},
            )
        }
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()
    }

    @Test
    fun on_setting_clicked_triggered() {
        val title = "Manage Subscription"
        val onSettingClicked: () -> Unit = mock()
        composeTestRule.setContent {
            SettingsManageSubscriptionItem(
                title = title,
                onSubscriptionClicked = onSettingClicked,
            )
        }
        composeTestRule
            .onNodeWithText(title)
            .performClick()
        verify(onSettingClicked).invoke()
    }
}
