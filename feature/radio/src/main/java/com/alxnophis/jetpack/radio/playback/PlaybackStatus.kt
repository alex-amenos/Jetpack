package com.alxnophis.jetpack.radio.playback

import androidx.media3.common.Player

internal enum class PlaybackStatus { Idle, Buffering, Playing, Paused, Error }

internal fun mapPlaybackState(
    playbackState: Int,
    isPlaying: Boolean,
    hasError: Boolean,
): PlaybackStatus =
    when {
        hasError -> PlaybackStatus.Error
        playbackState == Player.STATE_BUFFERING -> PlaybackStatus.Buffering
        playbackState == Player.STATE_READY && isPlaying -> PlaybackStatus.Playing
        playbackState == Player.STATE_READY -> PlaybackStatus.Paused
        else -> PlaybackStatus.Idle
    }
