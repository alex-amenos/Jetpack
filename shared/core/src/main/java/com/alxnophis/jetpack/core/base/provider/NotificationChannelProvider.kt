package com.alxnophis.jetpack.core.base.provider

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class NotificationChannelProvider(
    private val application: Application,
) {
    fun createNotificationChannel(
        channelId: String,
        channelName: String,
        notificationImportance: Int = NotificationManager.IMPORTANCE_DEFAULT,
    ) {
        val channel =
            NotificationChannel(
                channelId,
                channelName,
                notificationImportance,
            )
        val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val DEFAULT_NOTIFICATION_CHANNEL_ID = "Default"
        const val DEFAULT_NOTIFICATION_CHANNEL_NAME = "Default Communication"
    }
}
