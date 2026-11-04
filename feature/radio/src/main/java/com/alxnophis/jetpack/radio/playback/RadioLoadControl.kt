package com.alxnophis.jetpack.radio.playback

import androidx.media3.exoplayer.DefaultLoadControl

internal object RadioLoadControl {
    private const val LIVE_BUFFER_MS = 3_000

    fun create(): DefaultLoadControl =
        DefaultLoadControl
            .Builder()
            .setBufferDurationsMs(
                LIVE_BUFFER_MS,
                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                LIVE_BUFFER_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
            ).build()
}
