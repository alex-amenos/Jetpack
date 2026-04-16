package com.alxnophis.jetpack.utils

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown

/**
 * Screen Robot pattern for Posts screen
 * Provides a fluent API for interacting with the Posts feature
 */
class PostsScreenRobot(private val composeTestRule: ComposeTestRule) {

    fun waitForPostsToLoad(timeoutMillis: Long = 15000): PostsScreenRobot {
        composeTestRule.waitUntil(timeoutMillis) {
            composeTestRule
                .onAllNodesWithContentDescription("Post item")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        return this
    }

    fun clickPostAtIndex(index: Int): PostsScreenRobot {
        composeTestRule
            .onAllNodesWithContentDescription("Post item")[index]
            .performClick()
        return this
    }

    fun scrollToIndex(index: Int): PostsScreenRobot {
        composeTestRule
            .onNodeWithContentDescription("Posts list")
            .performScrollToIndex(index)
        return this
    }

    fun performPullToRefresh(): PostsScreenRobot {
        composeTestRule
            .onNodeWithContentDescription("Posts list")
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
            .onNodeWithContentDescription("Posts list")
            .assertIsDisplayed()
        return this
    }
}

/**
 * Screen Robot pattern for Post Detail screen
 */
class PostDetailScreenRobot(private val composeTestRule: ComposeTestRule) {

    fun waitForDetailToLoad(timeoutMillis: Long = 5000): PostDetailScreenRobot {
        composeTestRule.waitUntil(timeoutMillis) {
            composeTestRule
                .onAllNodesWithContentDescription("Post detail")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        return this
    }

    fun clickBack(): PostDetailScreenRobot {
        composeTestRule
            .onNodeWithContentDescription("Navigate back")
            .performClick()
        return this
    }

    fun assertDetailDisplayed(): PostDetailScreenRobot {
        composeTestRule
            .onNodeWithContentDescription("Post detail")
            .assertIsDisplayed()
        return this
    }
}

/**
 * Screen Robot pattern for Home screen
 */
class HomeScreenRobot(private val composeTestRule: ComposeTestRule) {

    fun waitForHomeScreen(timeoutMillis: Long = 5000): HomeScreenRobot {
        composeTestRule.waitUntil(timeoutMillis) {
            composeTestRule
                .onAllNodesWithText("Jetpack Project")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        return this
    }

    fun navigateToPosts(): PostsScreenRobot {
        composeTestRule
            .onNodeWithText("Posts")
            .performClick()
        
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("Posts")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        
        return PostsScreenRobot(composeTestRule)
    }

    fun assertHomeDisplayed(): HomeScreenRobot {
        composeTestRule
            .onNodeWithText("Jetpack Project")
            .assertIsDisplayed()
        return this
    }
}

/**
 * Extension function to create a HomeScreenRobot
 */
fun ComposeTestRule.homeScreen(): HomeScreenRobot = HomeScreenRobot(this)

/**
 * Extension function to create a PostsScreenRobot
 */
fun ComposeTestRule.postsScreen(): PostsScreenRobot = PostsScreenRobot(this)

/**
 * Extension function to create a PostDetailScreenRobot
 */
fun ComposeTestRule.postDetailScreen(): PostDetailScreenRobot = PostDetailScreenRobot(this)
