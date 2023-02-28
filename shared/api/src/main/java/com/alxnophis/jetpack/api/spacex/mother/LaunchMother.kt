package com.alxnophis.jetpack.api.spacex.mother

import com.alxnophis.jetpack.kotlin.constants.EMPTY
import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.alxnophis.jetpack.spacex.LaunchesQuery.Launch
import java.util.Date
import org.jetbrains.annotations.TestOnly

@TestOnly
object LaunchMother {

    operator fun invoke(
        id: String = EMPTY,
        missionName: String = EMPTY,
        details: String = EMPTY,
        rocketName: String = EMPTY,
        siteName: String = EMPTY,
        siteNameLong: String = EMPTY,
        missionPatchSmall: String = EMPTY,
        launchDateUtc: Date? = null
    ) = Launch(
        id = id,
        mission_name = missionName,
        details = details,
        rocket = LaunchesQuery.Rocket(
            rocket_name = rocketName
        ),
        launch_site = LaunchesQuery.Launch_site(
            site_name = siteName,
            site_name_long = siteNameLong
        ),
        links = LaunchesQuery.Links(mission_patch_small = missionPatchSmall),
        launch_date_utc = launchDateUtc
    )
}
