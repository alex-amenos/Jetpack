package com.alxnophis.jetpack.spacex.data.model

import java.util.Date

internal data class PastLaunchDataModel(
    val id: String,
    val missionName: String?,
    val details: String?,
    val rocketName: String?,
    val launchSiteShort: String?,
    val launchSite: String?,
    val missionPatchSmallUrl: String?,
    val launchDateUtc: Date?
)
