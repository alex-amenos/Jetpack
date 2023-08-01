@file:Suppress("unused", "KotlinConstantConditions")

package com.alxnophis.jetpack.core.extensions

import com.alxnophis.jetpack.core.BuildConfig

private const val BUILD_TYPE_DEBUG = "debug"
private const val BUILD_TYPE_RELEASE = "release"

fun isDebugBuildType(): Boolean = (BuildConfig.BUILD_TYPE == BUILD_TYPE_DEBUG)

fun isReleaseBuildType(): Boolean = (BuildConfig.BUILD_TYPE == BUILD_TYPE_RELEASE)
