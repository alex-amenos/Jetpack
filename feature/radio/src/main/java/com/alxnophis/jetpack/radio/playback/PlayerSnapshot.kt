package com.alxnophis.jetpack.radio.playback

import android.net.Uri

internal data class PlayerSnapshot(
    val stationId: String? = null,
    val stationName: String? = null,
    val artworkUri: Uri? = null,
    val status: PlaybackStatus = PlaybackStatus.Idle,
    val hasPrevious: Boolean = false,
    val hasNext: Boolean = false,
    val sleepTimerRemainingMillis: Long? = null,
)
