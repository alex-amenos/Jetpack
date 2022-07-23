package com.alxnophis.jetpack.spacex.ui.model

internal data class PastLaunchModel(
    val id: String,
    val missionName: String,
    val details: String,
    val rocket: String,
    val launchSite: String,
    val missionPatchUrl: String?,
    val launchDateUtc: String,
)
