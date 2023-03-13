package com.alxnophis.jetpack.settings.view.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.contract.Theme
import com.alxnophis.jetpack.settings.ui.view.SettingsContent
import com.alxnophis.jetpack.testing.constants.PAPARAZZI_MAX_PERCENT_DIFFERENCE
import org.junit.Rule
import org.junit.Test

internal class SettingsContentSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        maxPercentDifference = PAPARAZZI_MAX_PERCENT_DIFFERENCE
    )

    @Test
    fun composable_all_enabled() {
        snapshot(
            state = SettingsState(
                notificationsEnabled = true,
                hintsEnabled = true,
                marketingOption = MarketingOption.ALLOWED,
                themeOption = Theme.SYSTEM
            )
        )
    }

    @Test
    fun composable_all_disabled() {
        snapshot(
            state = SettingsState(
                notificationsEnabled = false,
                hintsEnabled = false,
                marketingOption = MarketingOption.NOT_ALLOWED,
                themeOption = Theme.LIGHT
            )
        )
    }

    private fun snapshot(
        state: SettingsState,
        appVersion: String = APP_VERSION
    ) {
        paparazzi.snapshot {
            SettingsContent(
                state = state,
                appVersion = appVersion,
                handleEvent = {},
                navigateBack = {}
            )
        }
    }

    companion object {
        private const val APP_VERSION = "1.0.0"
    }
}
