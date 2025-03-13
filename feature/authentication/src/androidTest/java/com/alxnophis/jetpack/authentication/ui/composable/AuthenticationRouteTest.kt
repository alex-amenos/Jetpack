package com.alxnophis.jetpack.authentication.ui.composable

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test

@ExperimentalComposeUiApi
class AuthenticationRouteTest : BaseComposeTest() {
    @Test
    fun sign_in_title_displayed_by_default() {
        authenticationContent(AuthenticationState.initialState)

        composeTestRule
            .onNodeWithText(context.getString(R.string.authentication_label_sign_in_to_account))
            .assertIsDisplayed()
    }

    @Test
    fun need_account_displayed_and_clickable() {
        authenticationContent(AuthenticationState.initialState)

        composeTestRule
            .onNodeWithText(context.getString(R.string.authentication_action_need_account))
            .assertIsDisplayed()
            .performClick()
    }

    @Test
    fun sign_in_title_displayed() {
        authenticationContent(
            AuthenticationState.initialState.copy(authenticationMode = AuthenticationMode.SIGN_IN),
        )

        composeTestRule
            .onNodeWithText(context.getString(R.string.authentication_label_sign_in_to_account))
            .assertIsDisplayed()
    }

    @Test
    fun sign_up_title_displayed() {
        authenticationContent(
            AuthenticationState.initialState.copy(authenticationMode = AuthenticationMode.SIGN_UP),
        )

        composeTestRule
            .onNodeWithText(context.getString(R.string.authentication_label_sign_up_for_account))
            .assertIsDisplayed()
    }

    private fun authenticationContent(state: AuthenticationState) {
        composeTestRule.setContent {
            AuthenticationContent(
                authenticationState = state,
                onEvent = {},
            )
        }
    }
}
