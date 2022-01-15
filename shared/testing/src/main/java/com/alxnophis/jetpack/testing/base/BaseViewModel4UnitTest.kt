package com.alxnophis.jetpack.testing.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.alxnophis.jetpack.testing.rules.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.Rule
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
abstract class BaseViewModel4UnitTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    fun runTest(block: suspend TestScope.() -> Unit) =
        testCoroutineRule.runTest { block() }
}
