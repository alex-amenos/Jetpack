package com.alxnophis.jetpack.radio.data.mapper

import com.alxnophis.jetpack.api.radiobrowser.model.StationApiModel
import com.alxnophis.jetpack.radio.domain.model.RadioStation

private const val HTTPS_SCHEME = "https"

internal fun StationApiModel.toRadioStationOrNull(): RadioStation? {
    val url = urlResolved?.takeIf { it.isNotBlank() } ?: return null
    if (!url.startsWith("$HTTPS_SCHEME://", ignoreCase = true)) return null
    return RadioStation(
        id = stationUuid,
        name = name,
        streamUrl = url,
        faviconUrl = favicon?.takeIf { it.isNotBlank() },
        codec = codec?.takeIf { it.isNotBlank() },
        bitrate = bitrate,
    )
}
