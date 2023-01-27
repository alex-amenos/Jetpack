package com.alxnophis.jetpack.spacex.ui.view

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesState
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchModel
import com.alxnophis.jetpack.testing.constants.PAPARAZZI_MAX_PERCENT_DIFFERENCE
import org.junit.Rule
import org.junit.Test

internal class SpacexContentSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        maxPercentDifference = PAPARAZZI_MAX_PERCENT_DIFFERENCE,
    )

    @Test
    fun composable_content_loaded() {
        snapshot(
            state = LaunchesState(
                isLoading = false,
                pastLaunches = listOf(LAUNCH_1, LAUNCH_2),
                errorMessages = emptyList()
            )
        )
    }

    @Test
    fun composable_loading() {
        snapshot(
            state = LaunchesState(
                isLoading = true,
                pastLaunches = emptyList(),
                errorMessages = emptyList()
            )
        )
    }

    @Test
    fun composable_error() {
        snapshot(
            state = LaunchesState(
                isLoading = false,
                pastLaunches = emptyList(),
                errorMessages = listOf(ERROR_MESSAGE)
            )
        )
    }

    private fun snapshot(state: LaunchesState) {
        paparazzi.snapshot {
            SpacexContent(
                state = state,
                handleEvent = {},
                navigateBack = {}
            )
        }
    }

    companion object {
        private const val LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        private val ERROR_MESSAGE = ErrorMessage(id = 1, message = "Error message")
        private val LAUNCH_1 = PastLaunchModel(
            id = "1",
            missionName = "Mission 1",
            details = LOREM_IPSUM.repeat(2),
            rocket = "Rocket Name 1 (Company 1)",
            launchSite = "Launch Site 1",
            missionPatchUrl = null,
            launchDateUtc = "10 May 21 - 11:00"
        )
        private val LAUNCH_2 = PastLaunchModel(
            id = "2",
            missionName = "Mission 2",
            details = LOREM_IPSUM.repeat(10),
            rocket = "Rocket Name 2 (Company 2)",
            launchSite = "Launch Site 2",
            missionPatchUrl = null,
            launchDateUtc = "15 Dec 31 - 12:00"
        )
    }
}
