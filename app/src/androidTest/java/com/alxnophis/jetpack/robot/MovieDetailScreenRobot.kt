package com.alxnophis.jetpack.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.alxnophis.jetpack.core.ui.composable.CoreTags

/**
 * Screen Robot for Movie Detail Screen
 *
 * Implements the Screen Robot pattern for the movie detail view.
 * Provides fluent API for viewing movie details and navigation.
 *
 * ## Test Tag Strategy
 * Uses [CoreTags] for stable element identification:
 * - [CoreTags.TAG_MOVIE_DETAIL]: The movie detail content container
 * - [CoreTags.TAG_CORE_BACK]: The back navigation button
 *
 * ## Usage Example
 * ```kotlin
 * composeTestRule
 *     .movieDetailScreen()
 *     .waitForDetailToLoad()
 *     .assertDetailDisplayed()
 *     .clickBack()
 * ```
 *
 * @property composeTestRule The Compose test rule for UI interactions
 * @see MoviesScreenRobot
 * @see com.alxnophis.jetpack.core.ui.composable.CoreTags
 */
class MovieDetailScreenRobot(
    private val composeTestRule: ComposeTestRule,
) {
    /**
     * Waits for the movie detail content to load and be displayed.
     * Uses [CoreTags.TAG_MOVIE_DETAIL] to detect when content is available.
     *
     * @param timeoutMillis Maximum time to wait in milliseconds (default: 5000ms)
     * @return This robot instance for method chaining
     */
    fun waitForDetailToLoad(timeoutMillis: Long = 5000): MovieDetailScreenRobot {
        composeTestRule.waitUntil(timeoutMillis) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_MOVIE_DETAIL)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        return this
    }

    /**
     * Clicks the back button to navigate away from movie detail.
     * Uses [CoreTags.TAG_CORE_BACK] to locate the back button.
     * After clicking, use [MoviesScreenRobot] to interact with the movies grid.
     *
     * @return This robot instance for method chaining
     */
    fun clickBack(): MovieDetailScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_CORE_BACK)
            .performClick()
        return this
    }

    /**
     * Asserts that the movie detail content is displayed.
     * Uses [CoreTags.TAG_MOVIE_DETAIL] to verify the detail view is visible.
     *
     * @return This robot instance for method chaining
     */
    fun assertDetailDisplayed(): MovieDetailScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_MOVIE_DETAIL)
            .assertIsDisplayed()
        return this
    }
}

/**
 * Extension function to create a [MovieDetailScreenRobot] from a [ComposeTestRule].
 *
 * Enables fluent API usage:
 * ```kotlin
 * composeTestRule.movieDetailScreen().waitForDetailToLoad()
 * ```
 *
 * @return A new [MovieDetailScreenRobot] instance
 */
fun ComposeTestRule.movieDetailScreen(): MovieDetailScreenRobot = MovieDetailScreenRobot(this)
