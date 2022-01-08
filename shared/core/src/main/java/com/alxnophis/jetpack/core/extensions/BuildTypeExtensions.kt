@file:Suppress("unused")

package com.alxnophis.jetpack.core.extensions

import com.alxnophis.jetpack.core.BuildConfig
import com.alxnophis.jetpack.core.extensions.BuildConstants.BUILD_TYPE_DEBUG
import com.alxnophis.jetpack.core.extensions.BuildConstants.BUILD_TYPE_RELEASE

private object BuildConstants {
    const val BUILD_TYPE_DEBUG = "debug"
    const val BUILD_TYPE_RELEASE = "release"
}

fun isDebugBuildType(): Boolean = (BuildConfig.BUILD_TYPE == BUILD_TYPE_DEBUG)

fun isReleaseBuildType(): Boolean = (BuildConfig.BUILD_TYPE == BUILD_TYPE_RELEASE)
