package com.alxnophis.jetpack.authentication.ui.view

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.alxnophis.jetpack.testing.constants.PAPARAZZI_MAX_PERCENT_DIFFERENCE
import org.junit.Rule
import org.junit.Test

internal class AuthorizedContentSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        maxPercentDifference = PAPARAZZI_MAX_PERCENT_DIFFERENCE,
    )

    @Test
    fun composable() {
        paparazzi.snapshot {
            AuthorizedContent(
                userEmail = EMAIL
            )
        }
    }

    companion object {
        private const val EMAIL = "your@email.com"
    }
}


