package com.alxnophis.jetpack.testing.base

import androidx.annotation.VisibleForTesting
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
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

/**
 * Module kotlinx-coroutines-test info:
 * https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-test#eagerly-entering-launch-and-async-blocks
 */
@ExperimentalCoroutinesApi
@Suppress("MemberVisibilityCanBePrivate", "unused")
@VisibleForTesting
open class BaseUnitTest {
    val testScheduler = TestCoroutineScheduler()
    val standardTestDispatcher = StandardTestDispatcher(testScheduler)
    val unconfinedTestDispatcher = UnconfinedTestDispatcher(testScheduler)
    val testDispatcherProvider = object : DispatcherProvider {
        override fun default(): CoroutineDispatcher = standardTestDispatcher
        override fun io(): CoroutineDispatcher = standardTestDispatcher
        override fun main(): CoroutineDispatcher = standardTestDispatcher
        override fun unconfined(): CoroutineDispatcher = standardTestDispatcher
    }
    val standardTestScope = TestScope(standardTestDispatcher)
    val unconfinedScope = TestScope(unconfinedTestDispatcher)

    @BeforeEach
    open fun beforeEach() {
        Dispatchers.setMain(standardTestDispatcher)
    }

    @AfterEach
    open fun afterEach() {
        Dispatchers.resetMain()
    }
}
