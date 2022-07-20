package com.alxnophis.jetpack.spacex.ui.model

internal data class PastLaunchesModel(
    val id: String,
    val mission_name: String,
    val details: String,
    val rocket: String,
    val launchSite: String,
    val mission_patch_url: String?,
    val launch_date_utc: String,
)
