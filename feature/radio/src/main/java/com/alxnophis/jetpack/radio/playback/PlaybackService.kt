package com.alxnophis.jetpack.radio.playback

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@UnstableApi
class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private lateinit var sleepTimer: RadioSleepTimer

    @SuppressLint("InlineCoroutineScope")
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        val audioAttributes =
            AudioAttributes
                .Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build()
        player =
            ExoPlayer
                .Builder(this)
                .setAudioAttributes(audioAttributes, true)
                .setLoadControl(RadioLoadControl.create())
                .setHandleAudioBecomingNoisy(true)
                .build()
        sleepTimer = RadioSleepTimer(serviceScope) { player.stop() }
        mediaSession =
            MediaSession
                .Builder(this, player)
                .setCallback(RadioSessionCallback())
                .setSessionActivity(openPlayerPendingIntent())
                .build()
        sleepTimer.remainingMillis
            .onEach { remaining -> publishRemaining(remaining) }
            .launchIn(serviceScope)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        sleepTimer.cancel()
        serviceScope.cancel()
        mediaSession?.run {
            release()
        }
        mediaSession = null
        player.release()
        super.onDestroy()
    }

    private fun openPlayerPendingIntent(): PendingIntent {
        val intent =
            packageManager
                .getLaunchIntentForPackage(packageName)
                ?.apply {
                    action = RadioPlayerIntent.ACTION_OPEN_PLAYER
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                } ?: Intent()
        return PendingIntent.getActivity(
            this,
            ZERO_INT,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private fun publishRemaining(remainingMillis: Long?) {
        val extras = Bundle().apply { remainingMillis?.let { putLong(RadioSessionCommands.KEY_REMAINING_MILLIS, it) } }
        mediaSession?.sessionExtras = extras
    }

    private inner class RadioSessionCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): MediaSession.ConnectionResult {
            val commands =
                MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS
                    .buildUpon()
                    .add(RadioSessionCommands.SET_SLEEP_TIMER)
                    .add(RadioSessionCommands.CANCEL_SLEEP_TIMER)
                    .build()
            return MediaSession.ConnectionResult
                .AcceptedResultBuilder(session, controller)
                .setAvailableSessionCommands(commands)
                .build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle,
        ): ListenableFuture<SessionResult> {
            when (customCommand) {
                RadioSessionCommands.SET_SLEEP_TIMER -> {
                    val minutes = args.getInt(RadioSessionCommands.KEY_MINUTES, ZERO_INT)
                    if (minutes > ZERO_INT) {
                        sleepTimer.start(minutes)
                    } else {
                        sleepTimer.cancel()
                    }
                }

                RadioSessionCommands.CANCEL_SLEEP_TIMER -> {
                    sleepTimer.cancel()
                }
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }
    }
}
