package com.alxnophis.jetpack.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import com.alxnophis.jetpack.core.ui.composable.CoreTags

/**
 * Screen Robot for Posts List Screen
 *
 * Implements the Screen Robot pattern for the posts list feature.
 * Provides fluent API for interactions like scrolling, clicking posts, and pull-to-refresh.
 *
 * ## Test Tag Strategy
 * Uses [CoreTags] for stable element identification:
 * - [CoreTags.TAG_POSTS_LIST]: The scrollable posts list container
 * - [CoreTags.TAG_POST_ITEM]: Individual post items in the list
 *
 * ## Usage Example
 * ```kotlin
 * composeTestRule
 *     .postsScreen()
 *     .waitForPostsToLoad()
 *     .assertPostItemsVisible()
 *     .scrollToIndex(5)
 *     .clickPostAtIndex(0)
 * ```
 *
 * @property composeTestRule The Compose test rule for UI interactions
 * @see PostDetailScreenRobot
 * @see com.alxnophis.jetpack.core.ui.composable.CoreTags
 */
class PostsScreenRobot(
    private val composeTestRule: ComposeTestRule,
) {
    /**
     * Waits for posts to load and be displayed in the list.
     * Uses [CoreTags.TAG_POST_ITEM] to detect when posts are available.
     *
     * @param timeoutMillis Maximum time to wait in milliseconds (default: 15000ms)
     * @return This robot instance for method chaining
     */
    fun waitForPostsToLoad(timeoutMillis: Long = 15000): PostsScreenRobot {
        composeTestRule.waitUntil(timeoutMillis) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        return this
    }

    /**
     * Clicks on a post item at the specified index.
     * After clicking, use [PostDetailScreenRobot] to interact with the detail screen.
     *
     * @param index Zero-based index of the post to click
     * @return This robot instance for method chaining
     */
    fun clickPostAtIndex(index: Int): PostsScreenRobot {
        composeTestRule
            .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)[index]
            .performClick()
        return this
    }

    /**
     * Scrolls the posts list to the specified index.
     * Useful for testing scrolling behavior and accessing posts off-screen.
     *
     * @param index Zero-based index to scroll to
     * @return This robot instance for method chaining
     */
    fun scrollToIndex(index: Int): PostsScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_POSTS_LIST)
            .performScrollToIndex(index)
        return this
    }

    /**
     * Performs a pull-to-refresh gesture on the posts list.
     * Triggers the refresh action by swiping down on the list.
     *
     * @return This robot instance for method chaining
     */
    fun performPullToRefresh(): PostsScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_POSTS_LIST)
            .performTouchInput {
                swipeDown()
            }
        return this
    }

    /**
     * Asserts that the posts screen title is displayed.
     *
     * @return This robot instance for method chaining
     */
    fun assertPostsDisplayed(): PostsScreenRobot {
        composeTestRule
            .onNodeWithText("Posts")
            .assertIsDisplayed()
        return this
    }

    /**
     * Asserts that the posts list container is visible.
     * Uses [CoreTags.TAG_POSTS_LIST] to verify the list is displayed.
     *
     * @return This robot instance for method chaining
     */
    fun assertPostItemsVisible(): PostsScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_POSTS_LIST)
            .assertIsDisplayed()
        return this
    }
}

/**
 * Extension function to create a [PostsScreenRobot] from a [ComposeTestRule].
 *
 * Enables fluent API usage:
 * ```kotlin
 * composeTestRule.postsScreen().waitForPostsToLoad()
 * ```
 *
 * @return A new [PostsScreenRobot] instance
 */
fun ComposeTestRule.postsScreen(): PostsScreenRobot = PostsScreenRobot(this)
