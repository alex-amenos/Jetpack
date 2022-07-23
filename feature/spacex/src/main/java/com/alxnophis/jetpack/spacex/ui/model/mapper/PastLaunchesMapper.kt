package com.alxnophis.jetpack.spacex.ui.model.mapper

import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.base.formatter.DateFormatter
import com.alxnophis.jetpack.spacex.data.model.PastLaunchDataModel
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchesModel

internal fun List<PastLaunchDataModel>.map(dateFormatter: DateFormatter): List<PastLaunchesModel> =
    this.map { launch ->
        PastLaunchesModel(
            id = launch.id,
            mission_name = launch.missionName ?: EMPTY,
            details = launch.details ?: EMPTY,
            rocket = launch.rocketName ?: EMPTY,
            launchSite = launch.launchSiteShort ?: EMPTY,
            mission_patch_url = launch.missionPatchSmallUrl,
            launch_date_utc = launch.launchDateUtc?.let { date -> dateFormatter.formatToReadableDateTime(date) } ?: EMPTY
        )
    }
