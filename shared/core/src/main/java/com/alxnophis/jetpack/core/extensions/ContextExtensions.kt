package com.alxnophis.jetpack.core.extensions

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import com.alxnophis.jetpack.kotlin.constants.ZERO_LONG

fun Context.getVersionName(): String =
    try {
        val packageInfo: PackageInfo =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(ZERO_LONG))
            } else {
                packageManager.getPackageInfo(packageName, ZERO_INT)
            }
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        EMPTY
    }

fun Context.getVersion(): String =
    StringBuilder()
        .append(this.getVersionName())
        .toString()

fun Context.showNotification(
    title: String,
    content: String,
    @DrawableRes icon: Int,
    channelId: String,
    notificationId: Int
) {
    val notificationManage = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification =
        NotificationCompat
            .Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(icon)
            .build()
    notificationManage.notify(notificationId, notification)
}
