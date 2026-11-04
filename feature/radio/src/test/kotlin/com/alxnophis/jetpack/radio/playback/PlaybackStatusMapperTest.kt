package com.alxnophis.jetpack.radio.playback

import androidx.media3.common.Player
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

internal class PlaybackStatusMapperTest {
    @Test
    fun `GIVEN buffering state WHEN mapped THEN returns Buffering`() {
        mapPlaybackState(Player.STATE_BUFFERING, isPlaying = false, hasError = false) shouldBeEqualTo PlaybackStatus.Buffering
    }

    @Test
    fun `GIVEN ready and playing WHEN mapped THEN returns Playing`() {
        mapPlaybackState(Player.STATE_READY, isPlaying = true, hasError = false) shouldBeEqualTo PlaybackStatus.Playing
    }

    @Test
    fun `GIVEN ready and not playing WHEN mapped THEN returns Paused`() {
        mapPlaybackState(Player.STATE_READY, isPlaying = false, hasError = false) shouldBeEqualTo PlaybackStatus.Paused
    }

    @Test
    fun `GIVEN an error WHEN mapped THEN returns Error even while buffering`() {
        mapPlaybackState(Player.STATE_BUFFERING, isPlaying = false, hasError = true) shouldBeEqualTo PlaybackStatus.Error
    }

    @Test
    fun `GIVEN idle state WHEN mapped THEN returns Idle`() {
        mapPlaybackState(Player.STATE_IDLE, isPlaying = false, hasError = false) shouldBeEqualTo PlaybackStatus.Idle
    }
}
