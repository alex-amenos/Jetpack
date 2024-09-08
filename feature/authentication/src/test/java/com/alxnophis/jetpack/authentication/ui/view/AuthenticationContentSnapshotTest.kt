package com.alxnophis.jetpack.authentication.ui.view

import androidx.compose.ui.ExperimentalComposeUiApi
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.contract.NO_ERROR
import com.alxnophis.jetpack.testing.constants.PAPARAZZI_MAX_PERCENT_DIFFERENCE
import org.junit.Rule
import org.junit.Test

@ExperimentalComposeUiApi
internal class AuthenticationContentSnapshotTest {
    @get:Rule
    val paparazzi =
        Paparazzi(
            deviceConfig = DeviceConfig.PIXEL_6,
            maxPercentDifference = PAPARAZZI_MAX_PERCENT_DIFFERENCE,
        )

    @Test
    fun composable_sign_in() {
        snapshot(
            state =
                AuthenticationState(
                    isUserAuthorized = false,
                    authenticationMode = AuthenticationMode.SIGN_IN,
                    email = EMAIL,
                    password = PASSWORD,
                    passwordRequirements = listOf(),
                    isLoading = false,
                    error = NO_ERROR,
                ),
        )
    }

    private fun snapshot(state: AuthenticationState) {
        paparazzi.snapshot {
            AuthenticationContent(
                authenticationState = state,
                onEvent = {},
            )
        }
    }

    companion object {
        private const val EMAIL = "your@email.com"
        private const val PASSWORD = "123456789Abc"
    }
}
