package com.alxnophis.jetpack.spacex.data.model.mapper

import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.alxnophis.jetpack.spacex.data.model.PastLaunchDataModel

internal fun LaunchesQuery.Data.map(): List<PastLaunchDataModel> =
    this
        .launches
        ?.filterNotNull()
        ?.filter { it.id != null }
        ?.map { launch ->
            PastLaunchDataModel(
                id = launch.id!!,
                missionName = launch.mission_name,
                details = launch.details,
                rocketName = launch.rocket?.rocket_name,
                launchSiteShort = launch.launch_site?.site_name,
                launchSite = launch.launch_site?.site_name_long,
                missionPatchSmallUrl = launch.links?.mission_patch_small,
                launchDateUtc = launch.launch_date_utc
            )
        }
        ?: emptyList()
