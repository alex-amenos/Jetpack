package com.alxnophis.jetpack.testing.base

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule

abstract class BaseComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val instrumentationContext: Context
        get() = InstrumentationRegistry.getInstrumentation().context
}
