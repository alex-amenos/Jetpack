package com.alxnophis.jetpack.spacex.data.datasource.mapper

import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.alxnophis.jetpack.spacex.data.model.PastLaunchesDataModel

fun LaunchesQuery.Data.map(): List<PastLaunchesDataModel> =
    this
        .launches
        ?.filterNotNull()
        ?.filter { it.id != null }
        ?.map { launch ->
            PastLaunchesDataModel(
                id = launch.id!!,
                mission_name = launch.mission_name ?: EMPTY,
                details = launch.details ?: EMPTY,
                rocketCompany = launch.rocket?.rocket?.company ?: EMPTY,
                rocketName = launch.rocket?.rocket?.name ?: EMPTY,
                launchSiteShort = launch.launch_site?.site_name ?: EMPTY,
                launchSite = launch.launch_site?.site_name ?: EMPTY,
                mission_patch_small_url = launch.links?.mission_patch_small,
                launch_date_utc = launch.launch_date_utc
            )
        }
        ?: emptyList()
