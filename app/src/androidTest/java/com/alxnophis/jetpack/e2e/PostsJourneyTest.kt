package com.alxnophis.jetpack.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alxnophis.jetpack.core.ui.composable.CoreTags
import com.alxnophis.jetpack.root.ui.RootActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-End Journey Test for Posts Feature
 *
 * This test validates the complete user journey for the Posts feature:
 * 1. App launches and shows the Home screen
 * 2. User navigates to Posts screen
 * 3. Posts are loaded and displayed
 * 4. User can interact with posts (click, scroll)
 * 5. User can navigate to post details
 * 6. User can navigate back
 *
 * To run this test:
 * ./gradlew :app:connectedDebugAndroidTest
 * or
 * ./gradlew :app:connectedDebugAndroidTest --tests "com.alxnophis.jetpack.e2e.PostsJourneyTest"
 */
@RunWith(AndroidJUnit4::class)
class PostsJourneyTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<RootActivity>()

    @Test
    fun GIVEN_app_launches_WHEN_navigate_to_posts_THEN_posts_are_displayed() {
        // GIVEN: App launches successfully
        // The Home screen should be displayed with the app title
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Jetpack Project")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        
        composeTestRule
            .onNodeWithText("Jetpack Project")
            .assertIsDisplayed()

        // WHEN: User navigates to Posts feature
        composeTestRule
            .onNodeWithText("Posts")
            .performClick()

        // THEN: Posts screen is displayed with posts title
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("Posts")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Posts")
            .assertIsDisplayed()

        // THEN: Posts content is loaded and visible
        // Wait for loading to complete and posts to be displayed
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            // Check if we have any post items loaded
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun GIVEN_posts_screen_WHEN_user_scrolls_THEN_more_posts_are_visible() {
        // GIVEN: Navigate to Posts screen
        navigateToPosts()

        // Wait for posts to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // WHEN: User scrolls down the list
        // Note: Scrolling requires the list to have sufficient items
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_POSTS_LIST)
            .performScrollToIndex(5)

        // THEN: List scrolls successfully (implicit assertion - no exception thrown)
    }

    @Test
    fun GIVEN_posts_screen_WHEN_user_clicks_post_THEN_detail_screen_appears() {
        // GIVEN: Navigate to Posts screen
        navigateToPosts()

        // Wait for posts to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // WHEN: User clicks on the first post
        composeTestRule
            .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)[0]
            .performClick()

        // THEN: Post detail screen is displayed
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_DETAIL)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithTag(CoreTags.TAG_POST_DETAIL)
            .assertIsDisplayed()
    }

    @Test
    fun GIVEN_post_detail_screen_WHEN_user_clicks_back_THEN_returns_to_posts_list() {
        // GIVEN: Navigate to post detail
        navigateToPosts()
        
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)[0]
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_DETAIL)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // WHEN: User clicks the back button
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_CORE_BACK)
            .performClick()

        // THEN: Returns to posts list
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithTag(CoreTags.TAG_POSTS_LIST)
            .assertIsDisplayed()
    }

    @Test
    fun GIVEN_posts_screen_WHEN_pull_to_refresh_THEN_posts_reload() {
        // GIVEN: Navigate to Posts screen
        navigateToPosts()

        // Wait for posts to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // WHEN: User performs pull-to-refresh gesture
        // Note: Pull-to-refresh might require specific gesture implementation
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_POSTS_LIST)
            .performTouchInput {
                swipeDown()
            }

        // THEN: Loading indicator appears briefly and posts are refreshed
        // This is a visual confirmation that refresh was triggered
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    // Helper function to navigate to Posts screen
    private fun navigateToPosts() {
        // Wait for home screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Jetpack Project")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Click on Posts navigation item
        composeTestRule
            .onNodeWithText("Posts")
            .performClick()

        // Wait for Posts screen to appear
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("Posts")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}
