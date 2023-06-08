package com.alxnophis.jetpack.testing.listener

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class MainCoroutineListener(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestListener {
    override suspend fun beforeSpec(spec: Spec) {
        Dispatchers.setMain(testDispatcher)
    }

    override suspend fun afterSpec(spec: Spec) {
        Dispatchers.setMain(testDispatcher)
    }
}
