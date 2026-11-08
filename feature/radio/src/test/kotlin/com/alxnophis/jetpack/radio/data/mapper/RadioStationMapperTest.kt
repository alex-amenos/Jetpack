package com.alxnophis.jetpack.radio.data.mapper

import com.alxnophis.jetpack.api.radiobrowser.model.RadioStationApiModel
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test

internal class RadioStationMapperTest {
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

    @Test
    fun `GIVEN a station with https favicon WHEN mapped THEN keeps the favicon url`() {
        val apiModel = apiModel(urlResolved = "https://example.com/stream.mp3", favicon = "https://example.com/ser.png")

        apiModel.toRadioStationOrNull()?.faviconUrl shouldBeEqualTo "https://example.com/ser.png"
    }

    @Test
    fun `GIVEN a station with http favicon WHEN mapped THEN drops the favicon url`() {
        val apiModel = apiModel(urlResolved = "https://example.com/stream.mp3", favicon = "http://example.com/ser.png")

        apiModel.toRadioStationOrNull()?.faviconUrl.shouldBeNull()
    }

    @Test
    fun `GIVEN a station with blank favicon WHEN mapped THEN drops the favicon url`() {
        val apiModel = apiModel(urlResolved = "https://example.com/stream.mp3", favicon = " ")

        apiModel.toRadioStationOrNull()?.faviconUrl.shouldBeNull()
    }

    private fun apiModel(
        urlResolved: String?,
        favicon: String? = "https://example.com/ser.png",
    ) = RadioStationApiModel(
        stationUuid = "uuid-1",
        name = "Cadena SER",
        urlResolved = urlResolved,
        favicon = favicon,
        codec = "MP3",
        bitrate = 128,
    )
}
