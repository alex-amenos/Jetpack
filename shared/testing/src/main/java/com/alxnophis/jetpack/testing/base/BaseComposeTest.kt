package com.alxnophis.jetpack.testing.base

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseComposeTest {

    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    val targetContext: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    val context: Context
        get() = InstrumentationRegistry.getInstrumentation().context

    fun assertStringDisplayed(text: String) {
        composeTestRule
            .onNodeWithText(text)
            .assertIsDisplayed()
    }

    fun assertStringResDisplayed(@StringRes stringResource: Int) {
        assertStringDisplayed(targetContext.getString(stringResource))
    }
}
