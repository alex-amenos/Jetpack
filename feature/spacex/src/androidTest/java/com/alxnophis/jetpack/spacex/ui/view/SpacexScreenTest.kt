package com.alxnophis.jetpack.spacex.ui.view

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.alxnophis.jetpack.core.ui.composable.CoreTags
import com.alxnophis.jetpack.spacex.R
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesState
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchesModelMother
import com.alxnophis.jetpack.spacex.ui.view.SpacexTags.TAG_SPACEX_LAUNCH_DETAIL
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test

class SpacexScreenTest : BaseComposeTest() {

    @Test
    fun spacex_top_bar_title_is_displayed() {
        setSpacexContent(state = LaunchesState())
        assertStringResDisplayed(R.string.spacex_title)
    }

    @Test
    fun spacex_top_bar_is_displayed_and_clickable() {
        setSpacexContent(state = LaunchesState())
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_CORE_BACK)
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun spacex_past_launches_date_is_displayed() {
        val date = "01 aug 2022 15:39"
        val pastLaunch = PastLaunchesModelMother.pastLaunch(launchDateUtc = date)
        setSpacexContent(state = LaunchesState(pastLaunches = listOf(pastLaunch)))
        assertStringDisplayed(date)
    }

    @Test
    fun spacex_past_launches_launch_site_is_displayed() {
        val pastLaunch = PastLaunchesModelMother.pastLaunch()
        setSpacexContent(state = LaunchesState(pastLaunches = listOf(pastLaunch)))
        assertStringDisplayed(pastLaunch.launchSite)
    }

    @Test
    fun spacex_past_launches_mission_name_is_displayed() {
        val pastLaunch = PastLaunchesModelMother.pastLaunch()
        setSpacexContent(state = LaunchesState(pastLaunches = listOf(pastLaunch)))
        assertStringDisplayed(pastLaunch.missionName)
    }

    @Test
    fun spacex_past_launches_rocket_name_is_displayed() {
        val pastLaunch = PastLaunchesModelMother.pastLaunch()
        setSpacexContent(state = LaunchesState(pastLaunches = listOf(pastLaunch)))
        assertStringDisplayed(pastLaunch.rocket)
    }

    @Test
    fun spacex_past_launches_detail_is_displayed_and_clickable() {
        val pastLaunch = PastLaunchesModelMother.pastLaunch(details = LONG_LOREM_IPSUM)
        setSpacexContent(state = LaunchesState(pastLaunches = listOf(pastLaunch)))
        composeTestRule
            .onNodeWithTag(TAG_SPACEX_LAUNCH_DETAIL + pastLaunch.id)
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun spacex_past_launches_detail_is_displayed_and_performs_click() {
        val pastLaunch = PastLaunchesModelMother.pastLaunch(details = LONG_LOREM_IPSUM)
        setSpacexContent(state = LaunchesState(pastLaunches = listOf(pastLaunch)))
        composeTestRule
            .onNodeWithTag(TAG_SPACEX_LAUNCH_DETAIL + pastLaunch.id)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
            .assertTextContains(LONG_LOREM_IPSUM)
    }

    private fun setSpacexContent(state: LaunchesState) {
        composeTestRule.setContent {
            SpacexContent(
                state = state,
                onLaunchesEvent = {},
                onNavigateBack = {}
            )
        }
    }

    companion object {
        private const val LONG_LOREM_IPSUM =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    }
}
