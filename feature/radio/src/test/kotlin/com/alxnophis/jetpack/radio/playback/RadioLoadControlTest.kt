package com.alxnophis.jetpack.radio.playback

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@UnstableApi
internal class RadioLoadControlTest {
    @Test
    fun `GIVEN the radio load control WHEN created THEN it is a DefaultLoadControl`() {
        RadioLoadControl.create().shouldBeInstanceOf<DefaultLoadControl>()
    }
}
