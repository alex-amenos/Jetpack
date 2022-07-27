package com.alxnophis.jetpack.testing.base

import androidx.annotation.VisibleForTesting
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
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
    open val testDispatcher = StandardTestDispatcher(testScheduler)
    open val testScope = TestScope(testDispatcher)
    val testDispatcherProvider = object : DispatcherProvider {
        override fun default(): CoroutineDispatcher = testDispatcher
        override fun io(): CoroutineDispatcher = testDispatcher
        override fun main(): CoroutineDispatcher = testDispatcher
        override fun unconfined(): CoroutineDispatcher = testDispatcher
    }

    @BeforeEach
    open fun beforeEach() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    open fun afterEach() {
        Dispatchers.resetMain()
    }
}
