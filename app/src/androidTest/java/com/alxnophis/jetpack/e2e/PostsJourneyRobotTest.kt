package com.alxnophis.jetpack.e2e

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alxnophis.jetpack.robot.homeScreen
import com.alxnophis.jetpack.robot.postDetailScreen
import com.alxnophis.jetpack.robot.postsScreen
import com.alxnophis.jetpack.root.ui.RootActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-End Journey Tests for Posts Feature using Screen Robot Pattern
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
 * │   ├── PostsScreenRobot
 * │   └── PostDetailScreenRobot
 * └── utils/                   Test utilities and helpers
 * ```
 *
 * ## Screen Robots
 * - [HomeScreenRobot]: Interacts with home screen (navigation, assertions)
 * - [PostsScreenRobot]: Interacts with posts list (scroll, click, refresh)
 * - [PostDetailScreenRobot]: Interacts with post detail (display, back navigation)
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
 * ./gradlew :app:connectedDebugAndroidTest --tests "com.alxnophis.jetpack.e2e.PostsJourneyRobotTest"
 *
 * # Run single test method
 * ./gradlew :app:connectedDebugAndroidTest --tests "*.PostsJourneyRobotTest.GIVEN_app_launches*"
 * ```
 *
 * ## Test Coverage
 * - App launch and navigation flow
 * - Posts loading and display
 * - Scrolling through long lists
 * - Navigation to post details
 * - Back navigation to posts list
 * - Pull-to-refresh functionality
 * - Complete user journey scenarios
 *
 * @see com.alxnophis.jetpack.robot.HomeScreenRobot
 * @see com.alxnophis.jetpack.robot.PostsScreenRobot
 * @see com.alxnophis.jetpack.robot.PostDetailScreenRobot
 * @see com.alxnophis.jetpack.core.ui.composable.CoreTags
 */
@RunWith(AndroidJUnit4::class)
class PostsJourneyRobotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<RootActivity>()

    @Test
    fun GIVEN_app_launches_WHEN_navigate_to_posts_THEN_posts_are_displayed() {
        composeTestRule
            .homeScreen()
            .waitForHomeScreen()
            .assertHomeDisplayed()
            .navigateToPosts()
            .waitForPostsToLoad()
            .assertPostsDisplayed()
            .assertPostItemsVisible()
    }

    @Test
    fun GIVEN_posts_screen_WHEN_user_scrolls_THEN_more_posts_are_visible() {
        composeTestRule
            .homeScreen()
            .waitForHomeScreen()
            .navigateToPosts()
            .waitForPostsToLoad()
            .scrollToIndex(5)
    }

    @Test
    fun GIVEN_posts_screen_WHEN_user_clicks_post_THEN_detail_screen_appears() {
        composeTestRule
            .homeScreen()
            .waitForHomeScreen()
            .navigateToPosts()
            .waitForPostsToLoad()
            .clickPostAtIndex(0)

        composeTestRule
            .postDetailScreen()
            .waitForDetailToLoad()
            .assertDetailDisplayed()
    }

    @Test
    fun GIVEN_post_detail_screen_WHEN_user_clicks_back_THEN_returns_to_posts_list() {
        composeTestRule
            .homeScreen()
            .waitForHomeScreen()
            .navigateToPosts()
            .waitForPostsToLoad()
            .clickPostAtIndex(0)

        composeTestRule
            .postDetailScreen()
            .waitForDetailToLoad()
            .clickBack()

        composeTestRule
            .postsScreen()
            .waitForPostsToLoad()
            .assertPostItemsVisible()
    }

    @Test
    fun GIVEN_posts_screen_WHEN_pull_to_refresh_THEN_posts_reload() {
        composeTestRule
            .homeScreen()
            .waitForHomeScreen()
            .navigateToPosts()
            .waitForPostsToLoad()
            .performPullToRefresh()
            .waitForPostsToLoad()
    }

    @Test
    fun GIVEN_app_WHEN_complete_user_journey_THEN_all_screens_work() {
        // Complete user journey: Home -> Posts -> Detail -> Back -> Posts
        composeTestRule
            .homeScreen()
            .waitForHomeScreen()
            .assertHomeDisplayed()
            .navigateToPosts()
            .waitForPostsToLoad()
            .assertPostsDisplayed()
            .clickPostAtIndex(0)

        composeTestRule
            .postDetailScreen()
            .waitForDetailToLoad()
            .assertDetailDisplayed()
            .clickBack()

        composeTestRule
            .postsScreen()
            .waitForPostsToLoad()
            .assertPostItemsVisible()
    }
}
