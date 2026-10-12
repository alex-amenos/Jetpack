package com.alxnophis.jetpack.core.base.provider

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mockConstruction
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

internal class NotificationChannelProviderUnitTests : BaseUnitTest() {
    private val application: Application = mock()
    private val notificationManager: NotificationManager = mock()

    @Test
    fun `GIVEN channel parameters WHEN createNotificationChannel THEN notification channel is registered`() {
        whenever(application.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(notificationManager)

        mockConstruction(NotificationChannel::class.java) { _, context ->
            context.arguments()[0] shouldBeEqualTo NotificationChannelProvider.DEFAULT_NOTIFICATION_CHANNEL_ID
            context.arguments()[1] shouldBeEqualTo NotificationChannelProvider.DEFAULT_NOTIFICATION_CHANNEL_NAME
            context.arguments()[2] shouldBeEqualTo NotificationManager.IMPORTANCE_HIGH
        }.use {
            val provider = NotificationChannelProvider(application)
            provider.createNotificationChannel(
                channelId = NotificationChannelProvider.DEFAULT_NOTIFICATION_CHANNEL_ID,
                channelName = NotificationChannelProvider.DEFAULT_NOTIFICATION_CHANNEL_NAME,
                notificationImportance = NotificationManager.IMPORTANCE_HIGH,
            )

            verify(notificationManager).createNotificationChannel(any())
        }
    }
}
