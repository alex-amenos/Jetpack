@file:OptIn(ExperimentalCoroutinesApi::class)

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
@Suppress("MemberVisibilityCanBePrivate", "unused")
@VisibleForTesting
abstract class BaseUnitTest {
    val testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testScope = TestScope(testDispatcher)
    val testDispatcherProvider = object : DispatcherProvider {
        override val default: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val main: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    @BeforeEach
    open fun beforeEach() {
        Dispatchers.setMain(testDispatcher)
        beforeEachCompleted()
    }

    open fun beforeEachCompleted() {}

    @AfterEach
    open fun afterEach() {
        Dispatchers.resetMain()
        afterEachCompleted()
    }

    open fun afterEachCompleted() {}
}
