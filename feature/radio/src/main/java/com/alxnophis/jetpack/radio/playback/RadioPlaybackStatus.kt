package com.alxnophis.jetpack.radio.playback

import androidx.media3.common.Player

internal enum class RadioPlaybackStatus { Idle, Buffering, Playing, Paused, Error }

internal fun mapPlaybackState(
    playbackState: Int,
    isPlaying: Boolean,
    hasError: Boolean,
): RadioPlaybackStatus =
    when {
        hasError -> RadioPlaybackStatus.Error
        playbackState == Player.STATE_BUFFERING -> RadioPlaybackStatus.Buffering
        playbackState == Player.STATE_READY && isPlaying -> RadioPlaybackStatus.Playing
        playbackState == Player.STATE_READY -> RadioPlaybackStatus.Paused
        else -> RadioPlaybackStatus.Idle
    }
