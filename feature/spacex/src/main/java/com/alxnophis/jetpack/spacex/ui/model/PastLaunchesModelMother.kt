package com.alxnophis.jetpack.spacex.ui.model

import com.alxnophis.jetpack.core.base.constants.EMPTY
import org.jetbrains.annotations.TestOnly

@TestOnly
internal object PastLaunchesModelMother {

    operator fun invoke(
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
