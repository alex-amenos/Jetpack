package com.alxnophis.jetpack.spacex.data.model

import java.util.Date

internal data class PastLaunchesDataModel(
    val id: String,
    val mission_name: String?,
    val details: String?,
    val rocketName: String?,
    val launchSiteShort: String?,
    val launchSite: String?,
    val mission_patch_small_url: String?,
    val launch_date_utc: Date?
)
