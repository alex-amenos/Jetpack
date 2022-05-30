package com.alxnophis.jetpack.testing.base

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@ExperimentalCoroutinesApi
@Suppress("MemberVisibilityCanBePrivate", "unused")
@VisibleForTesting
open class BaseUnitTest {

    val testScheduler = TestCoroutineScheduler()
    val standardTestDispatcher = StandardTestDispatcher(testScheduler)
    val unconfinedTestDispatcher = UnconfinedTestDispatcher(testScheduler)
    val testScope = TestScope(standardTestDispatcher)

    @BeforeEach
    open fun beforeEach() {
        Dispatchers.setMain(standardTestDispatcher)
    }

    @AfterEach
    open fun afterEach() {
        Dispatchers.resetMain()
    }
}
