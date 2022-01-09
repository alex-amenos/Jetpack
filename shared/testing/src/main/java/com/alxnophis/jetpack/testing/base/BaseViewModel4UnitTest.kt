package com.alxnophis.jetpack.testing.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.alxnophis.jetpack.testing.rules.TestCoroutineRule
import org.junit.Rule
import org.junit.rules.TestRule

abstract class BaseViewModel4UnitTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
}
