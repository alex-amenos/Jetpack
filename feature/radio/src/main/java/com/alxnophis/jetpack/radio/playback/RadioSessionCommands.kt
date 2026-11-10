package com.alxnophis.jetpack.radio.playback

import android.os.Bundle
import androidx.media3.session.SessionCommand

internal object RadioSessionCommands {
    const val KEY_MINUTES = "minutes"
    const val KEY_SLEEP_DEADLINE_MILLIS = "sleepDeadlineMillis"

    val SET_SLEEP_TIMER = SessionCommand("radio.SET_SLEEP_TIMER", Bundle.EMPTY)
    val CANCEL_SLEEP_TIMER = SessionCommand("radio.CANCEL_SLEEP_TIMER", Bundle.EMPTY)
}
