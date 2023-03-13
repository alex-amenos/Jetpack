package com.alxnophis.jetpack.spacex.data.model

import com.alxnophis.jetpack.core.base.constants.EMPTY
import java.util.Date
import org.jetbrains.annotations.TestOnly

@TestOnly
internal object PastLaunchesDataModelMother {

    operator fun invoke(
        id: String = EMPTY,
        missionName: String? = null,
        details: String? = null,
        rocketName: String? = null,
        launchSiteShort: String? = null,
        launchSite: String? = null,
        missionPatchSmallUrl: String? = null,
        launchDateUtc: Date? = null
    ) = PastLaunchDataModel(
        id = id,
        missionName = missionName,
        details = details,
        rocketName = rocketName,
        launchSiteShort = launchSiteShort,
        launchSite = launchSite,
        missionPatchSmallUrl = missionPatchSmallUrl,
        launchDateUtc = launchDateUtc
    )
}
