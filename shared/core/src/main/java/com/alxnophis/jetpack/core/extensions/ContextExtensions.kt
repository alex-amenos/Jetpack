package com.alxnophis.jetpack.core.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.alxnophis.jetpack.core.base.constants.BLANK
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.base.constants.PARENTHESES_CLOSED
import com.alxnophis.jetpack.core.base.constants.PARENTHESES_OPENED

fun Context.getVersionName(): String =
    try {
        packageManager.getPackageInfo(packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        EMPTY
    }

@Suppress("DEPRECATION")
fun Context.getVersionCode(): String =
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(packageName, 0).longVersionCode.toString()
        } else {
            packageManager.getPackageInfo(packageName, 0).versionCode.toString()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        EMPTY
    }

fun Context.getVersion(): String = StringBuilder()
    .append(this.getVersionName())
    .append(BLANK)
    .append(PARENTHESES_OPENED)
    .append(this.getVersionCode())
    .append(PARENTHESES_CLOSED)
    .toString()
