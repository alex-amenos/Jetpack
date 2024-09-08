package com.alxnophis.jetpack.testing.base

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseComposeTest {
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    val targetContext: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    val context: Context
        get() = InstrumentationRegistry.getInstrumentation().context
}
