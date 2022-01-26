package com.alxnophis.jetpack.testing.extensions

import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class, ExperimentalTime::class)
suspend fun <T> Flow<T>.testFix(
    timeout: Duration = 1.seconds,
    validate: suspend FlowTurbine<T>.() -> Unit,
) {
    val testScheduler = GlobalScope.coroutineContext[TestCoroutineScheduler]
    return if (testScheduler == null) {
        test(timeout, validate)
    } else {
        flowOn(UnconfinedTestDispatcher(testScheduler))
            .test(timeout, validate)
    }
}
