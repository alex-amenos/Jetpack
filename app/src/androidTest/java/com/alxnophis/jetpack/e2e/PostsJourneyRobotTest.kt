package com.alxnophis.jetpack.e2e

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alxnophis.jetpack.root.ui.RootActivity
import com.alxnophis.jetpack.utils.homeScreen
import com.alxnophis.jetpack.utils.postDetailScreen
import com.alxnophis.jetpack.utils.postsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-End Journey Test using Screen Robot Pattern
 *
 * This test demonstrates a cleaner, more maintainable approach to E2E testing
 * using the Screen Robot pattern for the Posts feature.
 *
 * To run this test:
 * ./gradlew :app:connectedDebugAndroidTest
 * or
 * ./gradlew :app:connectedDebugAndroidTest --tests "com.alxnophis.jetpack.e2e.PostsJourneyRobotTest"
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
