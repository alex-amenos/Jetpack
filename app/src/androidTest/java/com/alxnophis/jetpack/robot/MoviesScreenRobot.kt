package com.alxnophis.jetpack.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import com.alxnophis.jetpack.core.ui.composable.CoreTags

/**
 * Screen Robot for Movies List Screen
 *
 * Implements the Screen Robot pattern for the movies list feature.
 * Provides fluent API for interactions like scrolling, clicking movies, and searching.
 *
 * ## Test Tag Strategy
 * Uses [CoreTags] for stable element identification:
 * - [CoreTags.TAG_MOVIES_LIST]: The scrollable movies grid container
 * - [CoreTags.TAG_MOVIE_ITEM]: Individual movie items in the grid
 *
 * ## Usage Example
 * ```kotlin
 * composeTestRule
 *     .moviesScreen()
 *     .waitForMoviesToLoad()
 *     .assertMovieItemsVisible()
 *     .clickMovieAtIndex(0)
 * ```
 *
 * @property composeTestRule The Compose test rule for UI interactions
 * @see MovieDetailScreenRobot
 * @see com.alxnophis.jetpack.core.ui.composable.CoreTags
 */
class MoviesScreenRobot(
    private val composeTestRule: ComposeTestRule,
) {
    /**
     * Waits for movies to load and be displayed in the grid.
     * Uses [CoreTags.TAG_MOVIE_ITEM] to detect when movies are available.
     *
     * @param timeoutMillis Maximum time to wait in milliseconds (default: 15000ms)
     * @return This robot instance for method chaining
     */
    fun waitForMoviesToLoad(timeoutMillis: Long = 15000): MoviesScreenRobot {
        composeTestRule.waitUntil(timeoutMillis) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_MOVIE_ITEM)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        return this
    }

    /**
     * Clicks on a movie item at the specified index.
     * After clicking, use [MovieDetailScreenRobot] to interact with the detail screen.
     *
     * @param index Zero-based index of the movie to click
     * @return This robot instance for method chaining
     */
    fun clickMovieAtIndex(index: Int): MoviesScreenRobot {
        composeTestRule
            .onAllNodesWithTag(CoreTags.TAG_MOVIE_ITEM)[index]
            .performClick()
        return this
    }

    /**
     * Scrolls the movies grid to the specified index.
     *
     * @param index Zero-based index to scroll to
     * @return This robot instance for method chaining
     */
    fun scrollToIndex(index: Int): MoviesScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_MOVIES_LIST)
            .performScrollToIndex(index)
        return this
    }

    /**
     * Asserts that the movies screen title is displayed.
     *
     * @return This robot instance for method chaining
     */
    fun assertMoviesDisplayed(): MoviesScreenRobot {
        composeTestRule
            .onNodeWithText("Movies")
            .assertIsDisplayed()
        return this
    }

    /**
     * Asserts that the movies grid container is visible.
     * Uses [CoreTags.TAG_MOVIES_LIST] to verify the grid is displayed.
     *
     * @return This robot instance for method chaining
     */
    fun assertMovieItemsVisible(): MoviesScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_MOVIES_LIST)
            .assertIsDisplayed()
        return this
    }
}

/**
 * Extension function to create a [MoviesScreenRobot] from a [ComposeTestRule].
 *
 * Enables fluent API usage:
 * ```kotlin
 * composeTestRule.moviesScreen().waitForMoviesToLoad()
 * ```
 *
 * @return A new [MoviesScreenRobot] instance
 */
fun ComposeTestRule.moviesScreen(): MoviesScreenRobot = MoviesScreenRobot(this)
