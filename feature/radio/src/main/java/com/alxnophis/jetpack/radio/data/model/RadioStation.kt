package com.alxnophis.jetpack.radio.data.model

internal data class RadioStation(
    val id: String,
    val name: String,
    val streamUrl: String,
    val faviconUrl: String?,
    val codec: String?,
    val bitrate: Int?,
)
