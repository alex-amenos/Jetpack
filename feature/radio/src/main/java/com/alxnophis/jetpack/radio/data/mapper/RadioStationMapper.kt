package com.alxnophis.jetpack.radio.data.mapper

import com.alxnophis.jetpack.api.radiobrowser.model.RadioStationApiModel
import com.alxnophis.jetpack.radio.domain.model.RadioStation

private const val HTTPS_SCHEME = "https"

internal fun RadioStationApiModel.toRadioStationOrNull(): RadioStation? {
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
