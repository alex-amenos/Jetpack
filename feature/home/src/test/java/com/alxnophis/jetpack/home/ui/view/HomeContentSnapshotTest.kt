package com.alxnophis.jetpack.home.ui.view

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.home.ui.contract.HomeState
import com.alxnophis.jetpack.router.screen.Screen
import com.alxnophis.jetpack.testing.constants.PAPARAZZI_MAX_PERCENT_DIFFERENCE
import org.junit.Rule
import org.junit.Test

internal class HomeContentSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        maxPercentDifference = PAPARAZZI_MAX_PERCENT_DIFFERENCE
    )

    @Test
    fun composable() {
        snapshot(
            state = HomeState(
                data = listOf(NAVIGATION_ITEM_1, NAVIGATION_ITEM_2),
                error = null
            )
        )
    }

    private fun snapshot(state: HomeState) {
        paparazzi.snapshot {
            HomeContent(
                state = state,
                handleEvent = {},
                navigateTo = {}
            )
        }
    }

    companion object {
        private val NAVIGATION_ITEM_1 = NavigationItem(
            name = "Screen 1",
            emoji = "🐻",
            description = "Detail 1",
            screen = Screen.Authentication
        )
        private val NAVIGATION_ITEM_2 = NavigationItem(
            name = "Screen 2",
            emoji = "🦊",
            description = "Detail 2",
            screen = Screen.Settings
        )
    }
}
