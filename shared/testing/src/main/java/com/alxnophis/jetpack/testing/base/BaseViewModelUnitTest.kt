@file:OptIn(ExperimentalCoroutinesApi::class)

package com.alxnophis.jetpack.testing.base

import androidx.annotation.VisibleForTesting
import com.alxnophis.jetpack.testing.extensions.InstantExecutorExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@VisibleForTesting
@ExtendWith(InstantExecutorExtension::class)
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class BaseViewModelUnitTest {

    val testScheduler = TestCoroutineScheduler()
    open val testDispatcher = StandardTestDispatcher(testScheduler)
    val testScope = TestScope(testDispatcher)

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
