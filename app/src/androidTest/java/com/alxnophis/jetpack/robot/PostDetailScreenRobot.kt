package com.alxnophis.jetpack.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.alxnophis.jetpack.core.ui.composable.CoreTags

/**
 * Screen Robot for Post Detail Screen
 *
 * Implements the Screen Robot pattern for the post detail view.
 * Provides fluent API for viewing post details and navigation.
 *
 * ## Test Tag Strategy
 * Uses [CoreTags] for stable element identification:
 * - [CoreTags.TAG_POST_DETAIL]: The post detail content container
 * - [CoreTags.TAG_CORE_BACK]: The back navigation button
 *
 * ## Usage Example
 * ```kotlin
 * composeTestRule
 *     .postDetailScreen()
 *     .waitForDetailToLoad()
 *     .assertDetailDisplayed()
 *     .clickBack()
 * ```
 *
 * @property composeTestRule The Compose test rule for UI interactions
 * @see PostsScreenRobot
 * @see com.alxnophis.jetpack.core.ui.composable.CoreTags
 */
class PostDetailScreenRobot(
    private val composeTestRule: ComposeTestRule,
) {
    /**
     * Waits for the post detail content to load and be displayed.
     * Uses [CoreTags.TAG_POST_DETAIL] to detect when content is available.
     *
     * @param timeoutMillis Maximum time to wait in milliseconds (default: 5000ms)
     * @return This robot instance for method chaining
     */
    fun waitForDetailToLoad(timeoutMillis: Long = 5000): PostDetailScreenRobot {
        composeTestRule.waitUntil(timeoutMillis) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_DETAIL)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        return this
    }

    /**
     * Clicks the back button to navigate away from post detail.
     * Uses [CoreTags.TAG_CORE_BACK] to locate the back button.
     * After clicking, use [PostsScreenRobot] to interact with the posts list.
     *
     * @return This robot instance for method chaining
     */
    fun clickBack(): PostDetailScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_CORE_BACK)
            .performClick()
        return this
    }

    /**
     * Asserts that the post detail content is displayed.
     * Uses [CoreTags.TAG_POST_DETAIL] to verify the detail view is visible.
     *
     * @return This robot instance for method chaining
     */
    fun assertDetailDisplayed(): PostDetailScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_POST_DETAIL)
            .assertIsDisplayed()
        return this
    }
}

/**
 * Extension function to create a [PostDetailScreenRobot] from a [ComposeTestRule].
 *
 * Enables fluent API usage:
 * ```kotlin
 * composeTestRule.postDetailScreen().waitForDetailToLoad()
 * ```
 *
 * @return A new [PostDetailScreenRobot] instance
 */
fun ComposeTestRule.postDetailScreen(): PostDetailScreenRobot = PostDetailScreenRobot(this)
