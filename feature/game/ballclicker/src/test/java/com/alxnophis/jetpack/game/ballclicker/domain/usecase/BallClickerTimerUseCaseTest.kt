package com.alxnophis.jetpack.game.ballclicker.domain.usecase

import app.cash.turbine.test
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BallClickerTimerUseCaseTest : BaseUnitTest() {
    private val useCase = BallClickerTimerUseCase()

    @Test
    fun `GIVEN dispatcher context WHEN invoke timer THEN emits countdown values`() =
        runTest {
            val flow =
                context(testDispatcher) {
                    useCase(start = 3, end = 1)
                }

            flow.test {
                awaitItem() shouldBeEqualTo 3
                awaitItem() shouldBeEqualTo 2
                awaitItem() shouldBeEqualTo 1
                awaitComplete()
            }
        }
}
