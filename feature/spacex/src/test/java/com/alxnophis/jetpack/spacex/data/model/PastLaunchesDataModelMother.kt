package com.alxnophis.jetpack.spacex.data.model

import java.util.Date

internal object PastLaunchesDataModelMother {

    fun pastLaunch(
        id: String = "id",
        missionName: String? = "mission_name",
        details: String? = "details",
        rocketName: String? = "rocket_name",
        launchSiteShort: String? = "launch_site_name",
        launchSite: String? = "launch_site_name_long",
        missionPatchSmallUrl: String? = "mission_patch",
        launchDateUtc: Date? = null
    ) = PastLaunchDataModel(
        id = id,
        missionName = missionName,
        details = details,
        rocketName = rocketName,
        launchSiteShort = launchSiteShort,
        launchSite = launchSite,
        missionPatchSmallUrl = missionPatchSmallUrl,
        launchDateUtc = launchDateUtc,
    )
}
