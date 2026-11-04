package com.alxnophis.jetpack.radio.domain.model

internal data class Station(
    val id: String,
    val name: String,
    val streamUrl: String,
    val faviconUrl: String?,
    val codec: String?,
    val bitrate: Int?,
)
