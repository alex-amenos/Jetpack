package com.alxnophis.jetpack.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

/**
 * Screen Robot for Home Screen
 *
 * Implements the Screen Robot pattern (Page Object Model) for the application's home screen.
 * Provides a fluent API for test interactions and assertions.
 *
 * ## Usage Example
 * ```kotlin
 * composeTestRule
 *     .homeScreen()
 *     .waitForHomeScreen()
 *     .assertHomeDisplayed()
 *     .navigateToPosts()
 * ```
 *
 * ## Pattern Benefits
 * - **Fluent API**: Chainable methods for readable test code
 * - **Encapsulation**: Hides UI implementation details from tests
 * - **Reusability**: Single robot shared across multiple test cases
 * - **Maintainability**: UI changes only require updating the robot
 *
 * @property composeTestRule The Compose test rule for UI interactions
 * @see PostsScreenRobot
 * @see com.alxnophis.jetpack.e2e.PostsJourneyRobotTest
 */
class HomeScreenRobot(
    private val composeTestRule: ComposeTestRule,
) {
    /**
     * Waits for the home screen to be loaded and displayed.
     *
     * @param timeoutMillis Maximum time to wait in milliseconds (default: 5000ms)
     * @return This robot instance for method chaining
     */
    fun waitForHomeScreen(timeoutMillis: Long = 5000): HomeScreenRobot {
        composeTestRule.waitUntil(timeoutMillis) {
            composeTestRule
                .onAllNodesWithText("Jetpack Project")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        return this
    }

    /**
     * Navigates from home screen to the posts feature.
     * Waits for the posts screen to load after navigation.
     *
     * @return A new [PostsScreenRobot] instance for continued interaction
     */
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

    /**
     * Asserts that the home screen is currently displayed.
     *
     * @return This robot instance for method chaining
     */
    fun assertHomeDisplayed(): HomeScreenRobot {
        composeTestRule
            .onNodeWithText("Jetpack Project")
            .assertIsDisplayed()
        return this
    }
}

/**
 * Extension function to create a [HomeScreenRobot] from a [ComposeTestRule].
 *
 * Enables fluent API usage:
 * ```kotlin
 * composeTestRule.homeScreen().waitForHomeScreen()
 * ```
 *
 * @return A new [HomeScreenRobot] instance
 */
fun ComposeTestRule.homeScreen(): HomeScreenRobot = HomeScreenRobot(this)
