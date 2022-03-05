package com.alxnophis.jetpack.authentication.ui.view

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.testing.base.BaseComposeTest
import org.junit.Test

@ExperimentalComposeUiApi
class AuthenticationScreenTest : BaseComposeTest() {

    private val screenState = AuthenticationState()

    @Test
    fun sign_in_title_displayed_by_default() {
        composeTestRule.setContent {
            AuthenticationScreen(
                authenticationState = screenState,
                handleEvent = {}
            )
        }

        composeTestRule
            .onNodeWithText(targetContext.getString(R.string.authentication_label_sign_in_to_account))
            .assertIsDisplayed()
    }
}
