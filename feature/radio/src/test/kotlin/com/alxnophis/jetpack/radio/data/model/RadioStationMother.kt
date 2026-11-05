package com.alxnophis.jetpack.radio.data.model

internal object RadioStationMother {
    fun create(
        id: String = "station-1",
        name: String = "Cadena SER",
        streamUrl: String = "https://example.com/ser.mp3",
        faviconUrl: String? = "https://example.com/ser.png",
        codec: String? = "MP3",
        bitrate: Int? = 128,
    ) = RadioStation(
        id = id,
        name = name,
        streamUrl = streamUrl,
        faviconUrl = faviconUrl,
        codec = codec,
        bitrate = bitrate,
    )
}
