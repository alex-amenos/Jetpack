package com.alxnophis.jetpack.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

/**
 * Screen Robot pattern for Home screen
 * Provides a fluent API for interacting with the Home screen
 */
class HomeScreenRobot(
    private val composeTestRule: ComposeTestRule,
) {
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
