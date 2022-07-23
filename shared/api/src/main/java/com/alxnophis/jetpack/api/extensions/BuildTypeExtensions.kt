@file:Suppress("unused")

package com.alxnophis.jetpack.api.extensions

import com.alxnophis.jetpack.api.BuildConfig

private const val BUILD_TYPE_DEBUG = "debug"
private const val BUILD_TYPE_RELEASE = "release"

internal fun isDebugBuildType(): Boolean = (BuildConfig.BUILD_TYPE == BUILD_TYPE_DEBUG)

internal fun isReleaseBuildType(): Boolean = (BuildConfig.BUILD_TYPE == BUILD_TYPE_RELEASE)
