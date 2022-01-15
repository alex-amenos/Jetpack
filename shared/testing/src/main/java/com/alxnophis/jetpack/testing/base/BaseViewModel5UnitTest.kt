package com.alxnophis.jetpack.testing.base

import androidx.annotation.VisibleForTesting
import com.alxnophis.jetpack.testing.extensions.InstantExecutorExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@VisibleForTesting
@ExtendWith(InstantExecutorExtension::class)
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseViewModel5UnitTest {

    val testDispatcher = StandardTestDispatcher()
    val testScope = TestScope(testDispatcher)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    fun runTest(block: suspend TestScope.() -> Unit) =
        testScope.runTest { block() }
}
