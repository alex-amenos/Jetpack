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
 * Screen Robot pattern for Posts screen
 * Provides a fluent API for interacting with the Posts feature
 */
class PostsScreenRobot(
    private val composeTestRule: ComposeTestRule,
) {
    fun waitForPostsToLoad(timeoutMillis: Long = 15000): PostsScreenRobot {
        composeTestRule.waitUntil(timeoutMillis) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        return this
    }

    fun clickPostAtIndex(index: Int): PostsScreenRobot {
        composeTestRule
            .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)[index]
            .performClick()
        return this
    }

    fun scrollToIndex(index: Int): PostsScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_POSTS_LIST)
            .performScrollToIndex(index)
        return this
    }

    fun performPullToRefresh(): PostsScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_POSTS_LIST)
            .performTouchInput {
                swipeDown()
            }
        return this
    }

    fun assertPostsDisplayed(): PostsScreenRobot {
        composeTestRule
            .onNodeWithText("Posts")
            .assertIsDisplayed()
        return this
    }

    fun assertPostItemsVisible(): PostsScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_POSTS_LIST)
            .assertIsDisplayed()
        return this
    }
}

/**
 * Extension function to create a PostsScreenRobot
 */
fun ComposeTestRule.postsScreen(): PostsScreenRobot = PostsScreenRobot(this)
