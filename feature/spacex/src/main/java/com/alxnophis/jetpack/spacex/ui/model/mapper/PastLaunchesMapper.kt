package com.alxnophis.jetpack.spacex.ui.model.mapper

import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.base.formatter.DateFormatter
import com.alxnophis.jetpack.spacex.data.model.PastLaunchesDataModel
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchesModel

internal fun List<PastLaunchesDataModel>.map(dateFormatter: DateFormatter): List<PastLaunchesModel> =
    this.map { launch ->
        PastLaunchesModel(
            id = launch.id,
            mission_name = launch.mission_name ?: EMPTY,
            details = launch.details ?: EMPTY,
            rocket = launch.rocketName ?: EMPTY,
            launchSite = launch.launchSiteShort ?: EMPTY,
            mission_patch_url = launch.mission_patch_small_url,
            launch_date_utc = launch.launch_date_utc?.let { date -> dateFormatter.formatToReadableDateTime(date) } ?: EMPTY
        )
    }
