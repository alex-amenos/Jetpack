package com.alxnophis.jetpack.spacex.ui.model

import com.alxnophis.jetpack.core.base.constants.EMPTY
import org.jetbrains.annotations.TestOnly

@TestOnly
internal object PastLaunchesModelMother {

    operator fun invoke(
        id: String = EMPTY,
        missionName: String = EMPTY,
        details: String = EMPTY,
        rocketName: String = EMPTY,
        launchSiteName: String = EMPTY,
        missionPatchSmallUrl: String? = null,
        launchDateUtc: String = EMPTY
    ) = PastLaunchModel(
        id = id,
        missionName = missionName,
        details = details,
        rocket = rocketName,
        launchSite = launchSiteName,
        missionPatchUrl = missionPatchSmallUrl,
        launchDateUtc = launchDateUtc
    )
}
