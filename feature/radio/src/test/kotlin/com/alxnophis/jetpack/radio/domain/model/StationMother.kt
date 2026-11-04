package com.alxnophis.jetpack.radio.domain.model

internal object StationMother {
    fun create(
        id: String = "station-1",
        name: String = "Cadena SER",
        streamUrl: String = "https://example.com/ser.mp3",
        faviconUrl: String? = "https://example.com/ser.png",
        codec: String? = "MP3",
        bitrate: Int? = 128,
    ) = Station(
        id = id,
        name = name,
        streamUrl = streamUrl,
        faviconUrl = faviconUrl,
        codec = codec,
        bitrate = bitrate,
    )
}
