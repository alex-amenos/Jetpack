package com.alxnophis.jetpack.testing.base

import androidx.annotation.VisibleForTesting
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import com.alxnophis.jetpack.testing.extensions.InstantExecutorExtension
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@VisibleForTesting
@ExtendWith(InstantExecutorExtension::class)
@Suppress("MemberVisibilityCanBePrivate")
open class BaseViewModel5UnitTest {

    val testScheduler = TestCoroutineScheduler()
    val standardTestDispatcher = StandardTestDispatcher(testScheduler)
    val testDispatcherProvider = object : DispatcherProvider {
        override fun default(): CoroutineDispatcher = standardTestDispatcher
        override fun io(): CoroutineDispatcher = standardTestDispatcher
        override fun main(): CoroutineDispatcher = standardTestDispatcher
        override fun unconfined(): CoroutineDispatcher = standardTestDispatcher
    }

    @BeforeEach
    open fun beforeEach() {
        Dispatchers.setMain(standardTestDispatcher)
    }

    @AfterEach
    open fun afterEach() {
        Dispatchers.resetMain()
    }
}
