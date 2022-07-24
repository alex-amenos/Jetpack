package com.alxnophis.jetpack.spacex.mother

import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchModel

internal object PastLaunchesModelMother {

    fun pastLaunch(
        id: String = "id",
        missionName: String = "mission_name",
        details: String = "details",
        rocketName: String = "rocket_name",
        launchSiteName: String = "launch_site_name",
        missionPatchSmallUrl: String? = "mission_patch",
        launchDateUtc: String = EMPTY
    ) = PastLaunchModel(
        id = id,
        missionName = missionName,
        details = details,
        rocket = rocketName,
        launchSite = launchSiteName,
        missionPatchUrl = missionPatchSmallUrl,
        launchDateUtc = launchDateUtc,
    )
}
