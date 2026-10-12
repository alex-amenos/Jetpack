package com.alxnophis.jetpack.core.extensions

import app.cash.turbine.test
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class StateFlowExtensionsTest : BaseUnitTest() {
    @Test
    fun `GIVEN a value WHEN asStateFlow THEN returns StateFlow with initial value`() =
        runTest {
            val value = "test_value"
            val stateFlow = value.asStateFlow()

            stateFlow.test {
                awaitItem() shouldBeEqualTo value
                expectNoEvents()
            }
        }

    @Test
    fun `GIVEN a value WHEN asMutableStateFlow THEN returns MutableStateFlow and allows updates`() =
        runTest {
            val value = 10
            val mutableStateFlow = value.asMutableStateFlow()

            mutableStateFlow.test {
                awaitItem() shouldBeEqualTo 10
                mutableStateFlow.value = 20
                awaitItem() shouldBeEqualTo 20
                expectNoEvents()
            }
        }
}
