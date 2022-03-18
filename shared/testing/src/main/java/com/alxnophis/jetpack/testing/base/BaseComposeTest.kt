package com.alxnophis.jetpack.testing.base

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule

abstract class BaseComposeTest {

    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    val targetContext: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    val context: Context
        get() = InstrumentationRegistry.getInstrumentation().context

    fun assertStringDisplayedWith(
        @StringRes stringResource: Int,
        composable: @Composable () -> Unit,
    ) {
        composeTestRule.setContent {
            composable()
        }
        composeTestRule
            .onNodeWithText(targetContext.getString(stringResource))
            .assertIsDisplayed()
    }
}
