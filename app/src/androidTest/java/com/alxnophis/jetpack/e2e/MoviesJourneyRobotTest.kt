package com.alxnophis.jetpack.e2e

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alxnophis.jetpack.robot.homeScreen
import com.alxnophis.jetpack.robot.movieDetailScreen
import com.alxnophis.jetpack.robot.moviesScreen
import com.alxnophis.jetpack.root.ui.RootActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-End Journey Tests for Movies Feature using Screen Robot Pattern
 *
 * ## Architecture
 * These tests validate complete user journeys using the Screen Robot Pattern (Page Object Model).
 * Screen Robots provide a fluent API that encapsulates UI interactions and assertions,
 * making tests more readable, maintainable, and reusable.
 *
 * ## Test Structure
 * ```
 * app/src/androidTest/
 * ├── e2e/                     End-to-end journey tests
 * ├── robot/                   Screen Robot implementations
 * │   ├── HomeScreenRobot
 * │   ├── MoviesScreenRobot
 * │   └── MovieDetailScreenRobot
 * └── utils/                   Test utilities and helpers
 * ```
 *
 * ## Screen Robots
 * - [HomeScreenRobot]: Interacts with home screen (navigation, assertions)
 * - [MoviesScreenRobot]: Interacts with movies grid (scroll, click)
 * - [MovieDetailScreenRobot]: Interacts with movie detail (display, back navigation)
 *
 * ## Test Identification Strategy
 * Uses [CoreTags] for stable test identifiers via `testTag()` instead of `contentDescription`.
 * This decouples tests from accessibility strings, allowing:
 * - Localized accessibility labels for real users
 * - Stable test selectors independent of UI text changes
 *
 * ## Running Tests
 * ```bash
 * # Run all E2E tests
 * ./gradlew :app:connectedDebugAndroidTest
 *
 * # Run this specific test class
 * ./gradlew :app:connectedDebugAndroidTest --tests "com.alxnophis.jetpack.e2e.MoviesJourneyRobotTest"
 *
 * # Run single test method
 * ./gradlew :app:connectedDebugAndroidTest --tests "*.MoviesJourneyRobotTest.GIVEN_app_launches*"
 * ```
 *
 * ## Test Coverage
 * - App launch and navigation flow
 * - Movies loading and display
 * - Scrolling through the movies grid
 * - Navigation to movie details
 * - Back navigation to movies grid
 * - Complete user journey scenarios
 *
 * @see com.alxnophis.jetpack.robot.HomeScreenRobot
 * @see com.alxnophis.jetpack.robot.MoviesScreenRobot
 * @see com.alxnophis.jetpack.robot.MovieDetailScreenRobot
 * @see com.alxnophis.jetpack.core.ui.composable.CoreTags
 */
@RunWith(AndroidJUnit4::class)
class MoviesJourneyRobotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<RootActivity>()

    @Test
    fun GIVEN_app_launches_WHEN_navigate_to_movies_THEN_movies_are_displayed() {
        composeTestRule
            .homeScreen()
            .waitForHomeScreen()
            .assertHomeDisplayed()
            .navigateToMovies()
            .waitForMoviesToLoad()
            .assertMoviesDisplayed()
            .assertMovieItemsVisible()
    }

    @Test
    fun GIVEN_movies_screen_WHEN_user_scrolls_THEN_more_movies_are_visible() {
        composeTestRule
            .homeScreen()
            .waitForHomeScreen()
            .navigateToMovies()
            .waitForMoviesToLoad()
            .scrollToIndex(2)
    }

    @Test
    fun GIVEN_movies_screen_WHEN_user_clicks_movie_THEN_detail_screen_appears() {
        composeTestRule
            .homeScreen()
            .waitForHomeScreen()
            .navigateToMovies()
            .waitForMoviesToLoad()
            .clickMovieAtIndex(0)

        composeTestRule
            .movieDetailScreen()
            .waitForDetailToLoad()
            .assertDetailDisplayed()
    }

    @Test
    fun GIVEN_movie_detail_screen_WHEN_user_clicks_back_THEN_returns_to_movies_grid() {
        composeTestRule
            .homeScreen()
            .waitForHomeScreen()
            .navigateToMovies()
            .waitForMoviesToLoad()
            .clickMovieAtIndex(0)

        composeTestRule
            .movieDetailScreen()
            .waitForDetailToLoad()
            .clickBack()

        composeTestRule
            .moviesScreen()
            .waitForMoviesToLoad()
            .assertMovieItemsVisible()
    }

    @Test
    fun GIVEN_app_WHEN_complete_user_journey_THEN_all_screens_work() {
        // Complete user journey: Home -> Movies -> Detail -> Back -> Movies
        composeTestRule
            .homeScreen()
            .waitForHomeScreen()
            .assertHomeDisplayed()
            .navigateToMovies()
            .waitForMoviesToLoad()
            .assertMoviesDisplayed()
            .clickMovieAtIndex(0)

        composeTestRule
            .movieDetailScreen()
            .waitForDetailToLoad()
            .assertDetailDisplayed()
            .clickBack()

        composeTestRule
            .moviesScreen()
            .waitForMoviesToLoad()
            .assertMovieItemsVisible()
    }
}
