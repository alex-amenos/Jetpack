package com.alxnophis.jetpack.api.radiobrowser.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationApiModel(
    @SerialName("stationuuid") val stationUuid: String,
    @SerialName("name") val name: String,
    @SerialName("url_resolved") val urlResolved: String? = null,
    @SerialName("favicon") val favicon: String? = null,
    @SerialName("codec") val codec: String? = null,
    @SerialName("bitrate") val bitrate: Int? = null,
)
