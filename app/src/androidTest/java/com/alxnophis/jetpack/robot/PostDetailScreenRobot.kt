package com.alxnophis.jetpack.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.alxnophis.jetpack.core.ui.composable.CoreTags

/**
 * Screen Robot pattern for Post Detail screen
 * Provides a fluent API for interacting with the Post Detail feature
 */
class PostDetailScreenRobot(
    private val composeTestRule: ComposeTestRule,
) {
    fun waitForDetailToLoad(timeoutMillis: Long = 5000): PostDetailScreenRobot {
        composeTestRule.waitUntil(timeoutMillis) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_DETAIL)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        return this
    }

    fun clickBack(): PostDetailScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_CORE_BACK)
            .performClick()
        return this
    }

    fun assertDetailDisplayed(): PostDetailScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_POST_DETAIL)
            .assertIsDisplayed()
        return this
    }
}

/**
 * Extension function to create a PostDetailScreenRobot
 */
fun ComposeTestRule.postDetailScreen(): PostDetailScreenRobot = PostDetailScreenRobot(this)
