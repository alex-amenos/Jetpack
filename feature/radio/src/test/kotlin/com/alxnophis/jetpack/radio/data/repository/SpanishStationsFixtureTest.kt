package com.alxnophis.jetpack.radio.data.repository

import com.alxnophis.jetpack.api.radiobrowser.model.StationApiModel
import com.alxnophis.jetpack.radio.data.mapper.toStationOrNull
import kotlinx.serialization.json.Json
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

internal class SpanishStationsFixtureTest {
    @Test
    fun `GIVEN the spanish stations fixture WHEN mapped THEN every station has a playable https stream`() {
        val json = Json { ignoreUnknownKeys = true }
        val text = javaClass.classLoader!!.getResource("spanish_stations.json")!!.readText()
        val stations = json.decodeFromString<List<StationApiModel>>(text).mapNotNull { it.toStationOrNull() }

        stations.size shouldBeEqualTo 50
        stations.all { it.streamUrl.startsWith("https://") } shouldBeEqualTo true
    }
}
