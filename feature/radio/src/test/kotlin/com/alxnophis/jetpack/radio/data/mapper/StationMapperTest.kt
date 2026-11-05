package com.alxnophis.jetpack.radio.data.mapper

import com.alxnophis.jetpack.api.radiobrowser.model.StationApiModel
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test

internal class StationMapperTest {
    @Test
    fun `GIVEN a station with blank url_resolved WHEN mapped THEN returns null`() {
        val apiModel = apiModel(urlResolved = "   ")

        apiModel.toRadioStationOrNull().shouldBeNull()
    }

    @Test
    fun `GIVEN a station with null url_resolved WHEN mapped THEN returns null`() {
        val apiModel = apiModel(urlResolved = null)

        apiModel.toRadioStationOrNull().shouldBeNull()
    }

    @Test
    fun `GIVEN a station with http url WHEN mapped THEN returns null`() {
        val apiModel = apiModel(urlResolved = "http://example.com:8000/stream")

        apiModel.toRadioStationOrNull().shouldBeNull()
    }

    @Test
    fun `GIVEN a station with https url WHEN mapped THEN returns station with same id, name and stream url`() {
        val apiModel = apiModel(urlResolved = "https://example.com/stream.mp3")

        val station = apiModel.toRadioStationOrNull()

        station?.id shouldBeEqualTo "uuid-1"
        station?.name shouldBeEqualTo "Cadena SER"
        station?.streamUrl shouldBeEqualTo "https://example.com/stream.mp3"
    }

    private fun apiModel(urlResolved: String?) =
        StationApiModel(
            stationUuid = "uuid-1",
            name = "Cadena SER",
            urlResolved = urlResolved,
            favicon = "https://example.com/ser.png",
            codec = "MP3",
            bitrate = 128,
        )
}
