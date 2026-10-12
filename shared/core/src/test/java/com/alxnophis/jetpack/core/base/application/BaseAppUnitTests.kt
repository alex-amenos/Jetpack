package com.alxnophis.jetpack.core.base.application

import com.alxnophis.jetpack.testing.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class BaseAppUnitTests : BaseUnitTest() {
    @Test
    fun `GIVEN test dispatcher WHEN initNotificationChannels THEN channels are created asynchronously on dispatcher`() {
        val scheduler = TestCoroutineScheduler()
        val testDispatcher = StandardTestDispatcher(scheduler)
        var channelsCreatedCount = 0

        val app =
            object : BaseApp(ioDispatcher = testDispatcher) {
                public override fun initNotificationChannels() {
                    super.initNotificationChannels()
                }

                override fun createNotificationChannels() {
                    channelsCreatedCount++
                }
            }

        app.initNotificationChannels()

        channelsCreatedCount shouldBeEqualTo 0

        scheduler.runCurrent()

        channelsCreatedCount shouldBeEqualTo 1
    }
}
