package com.alxnophis.jetpack.testing.rules

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalCoroutinesApi
class TestCoroutineRule : TestRule {

    val testDispatcher = StandardTestDispatcher()
    val testScope = TestScope(this.testDispatcher)

    override fun apply(base: Statement, description: Description?) = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(this@TestCoroutineRule.testDispatcher)

            base.evaluate()

            Dispatchers.resetMain()
        }
    }

    fun runTest(block: suspend TestScope.() -> Unit) =
        this.testScope.runTest { block() }
}
