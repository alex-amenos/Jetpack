package com.alxnophis.jetpack.radio.playback

import com.alxnophis.jetpack.radio.domain.model.Station
import kotlinx.coroutines.flow.StateFlow

internal interface RadioPlayer {
    val snapshot: StateFlow<PlayerSnapshot>

    fun connect()

    fun playQueue(
        stations: List<Station>,
        startIndex: Int,
    )

    fun play()

    fun pause()

    fun stop()

    fun next()

    fun previous()

    fun retry()

    fun setSleepTimer(minutes: Int?)

    fun release()
}
