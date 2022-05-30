package com.alxnophis.jetpack.testing.base

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@ExperimentalCoroutinesApi
@Suppress("MemberVisibilityCanBePrivate", "unused")
@VisibleForTesting
open class BaseUnitTest {

    val standardTestDispatcher = StandardTestDispatcher()
    val testScope = TestScope(standardTestDispatcher)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(standardTestDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
