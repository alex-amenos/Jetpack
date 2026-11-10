package com.alxnophis.jetpack.radio.playback

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl

internal object RadioLoadControl {
    private const val MIN_BUFFER_MS = 3_000
    private const val BUFFER_FOR_PLAYBACK_MS = 3_000

    @UnstableApi
    fun create(): DefaultLoadControl =
        DefaultLoadControl
            .Builder()
            .setBufferDurationsMs(
                MIN_BUFFER_MS,
                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
            ).build()
}
