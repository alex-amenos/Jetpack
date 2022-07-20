package com.alxnophis.jetpack.spacex.data.model.mapper

import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.alxnophis.jetpack.spacex.data.model.PastLaunchesDataModel

internal fun LaunchesQuery.Data.map(): List<PastLaunchesDataModel> =
    this
        .launches
        ?.filterNotNull()
        ?.filter { it.id != null }
        ?.map { launch ->
            PastLaunchesDataModel(
                id = launch.id!!,
                mission_name = launch.mission_name,
                details = launch.details,
                rocketCompany = launch.rocket?.rocket?.company,
                rocketName = launch.rocket?.rocket?.name,
                launchSiteShort = launch.launch_site?.site_name,
                launchSite = launch.launch_site?.site_name,
                mission_patch_small_url = launch.links?.mission_patch_small,
                launch_date_utc = launch.launch_date_utc
            )
        }
        ?: emptyList()
