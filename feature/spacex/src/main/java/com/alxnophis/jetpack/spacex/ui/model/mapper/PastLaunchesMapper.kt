package com.alxnophis.jetpack.spacex.ui.model.mapper

import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.base.formatter.DateFormatter
import com.alxnophis.jetpack.spacex.data.model.PastLaunchDataModel
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchModel

internal fun List<PastLaunchDataModel>.map(dateFormatter: DateFormatter): List<PastLaunchModel> =
    this.map { launch ->
        PastLaunchModel(
            id = launch.id,
            missionName = launch.missionName ?: EMPTY,
            details = launch.details ?: EMPTY,
            rocket = launch.rocketName ?: EMPTY,
            launchSite = launch.launchSiteShort ?: EMPTY,
            missionPatchUrl = launch.missionPatchSmallUrl,
            launchDateUtc = launch.launchDateUtc?.let { date -> dateFormatter.formatToReadableDateTime(date) } ?: EMPTY
        )
    }
