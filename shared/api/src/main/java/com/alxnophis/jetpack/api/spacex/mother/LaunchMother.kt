package com.alxnophis.jetpack.api.spacex.mother

import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.alxnophis.jetpack.spacex.LaunchesQuery.Launch
import java.util.Date
import org.jetbrains.annotations.TestOnly

@TestOnly
object LaunchMother {

    operator fun invoke(
        id: String = "id",
        missionName: String = "mission_name",
        details: String = "details",
        rocketName: String = "rocket_name",
        siteName: String = "launch_site_name",
        siteNameLong: String = "launch_site_name_long",
        missionPatchSmall: String = "mission_patch",
        launchDateUtc: Date? = null,
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
