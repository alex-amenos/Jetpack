package com.alxnophis.jetpack.radio.playback

import androidx.media3.common.util.UnstableApi
import com.alxnophis.jetpack.radio.data.model.RadioStationMother
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@UnstableApi
internal class RadioStationMediaItemMapperTest {
    @Test
    fun `GIVEN a station WHEN mapped THEN the media id and uri are the station id and stream url`() {
        val mediaItem = RadioStationMother.create(id = "uuid-9", streamUrl = "https://example.com/live.m3u8").toMediaItem()

        mediaItem.mediaId shouldBeEqualTo "uuid-9"
        mediaItem.localConfiguration?.uri.toString() shouldBeEqualTo "https://example.com/live.m3u8"
    }

    @Test
    fun `GIVEN a station WHEN mapped THEN the title is the station name and the artist is the source`() {
        val mediaItem = RadioStationMother.create(name = "Cadena SER").toMediaItem()

        mediaItem.mediaMetadata.title.toString() shouldBeEqualTo "Cadena SER"
        mediaItem.mediaMetadata.artist.toString() shouldBeEqualTo "Radio Browser - Spain"
    }

    @Test
    fun `GIVEN a station with favicon WHEN mapped THEN the artwork uri is the favicon`() {
        val mediaItem = RadioStationMother.create(faviconUrl = "https://example.com/ser.png").toMediaItem()

        mediaItem.mediaMetadata.artworkUri.toString() shouldBeEqualTo "https://example.com/ser.png"
    }

    @Test
    fun `GIVEN a station without favicon WHEN mapped THEN there is no artwork uri`() {
        val mediaItem = RadioStationMother.create(faviconUrl = null).toMediaItem()

        mediaItem.mediaMetadata.artworkUri.shouldBeNull()
    }

    @Test
    fun `GIVEN a station WHEN mapped THEN the live playback speed is fixed at 1x`() {
        val mediaItem = RadioStationMother.create().toMediaItem()

        mediaItem.liveConfiguration.maxPlaybackSpeed shouldBeEqualTo 1.0f
    }
}
