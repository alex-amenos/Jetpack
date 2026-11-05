package com.alxnophis.jetpack.radio.playback

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.alxnophis.jetpack.radio.data.model.RadioStation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class RadioPlayerController(
    private val context: Context,
) : RadioPlayer {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val controllerDeferred = CompletableDeferred<MediaController>()
    private var activeController: MediaController? = null
    private var connected = false
    private val _snapshot = MutableStateFlow(RadioPlayerSnapshot())
    override val snapshot: StateFlow<RadioPlayerSnapshot> = _snapshot.asStateFlow()

    override fun connect() {
        if (connected) return
        connected = true
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future =
            MediaController
                .Builder(context, token)
                .setListener(
                    object : MediaController.Listener {
                        override fun onExtrasChanged(
                            controller: MediaController,
                            extras: Bundle,
                        ) {
                            publish(controller)
                        }
                    },
                ).buildAsync()
        future.addListener(
            {
                val controller = future.get()
                controller.addListener(
                    object : Player.Listener {
                        override fun onEvents(
                            player: Player,
                            events: Player.Events,
                        ) {
                            publish(controller)
                        }
                    },
                )
                activeController = controller
                controllerDeferred.complete(controller)
                publish(controller)
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    override fun playQueue(
        stations: List<RadioStation>,
        startIndex: Int,
    ) = withController { controller ->
        controller.setMediaItems(stations.map { it.toMediaItem() }, startIndex, 0L)
        controller.prepare()
        controller.play()
    }

    override fun play() =
        withController { controller ->
            if (controller.playbackState == Player.STATE_IDLE) controller.prepare()
            controller.play()
        }

    override fun pause() = withController { it.pause() }

    override fun stop() = withController { it.stop() }

    override fun next() = withController { it.seekToNextMediaItem() }

    override fun previous() = withController { it.seekToPreviousMediaItem() }

    override fun retry() =
        withController { controller ->
            controller.prepare()
            controller.play()
        }

    override fun setSleepTimer(minutes: Int?) =
        withController { controller ->
            val command = if (minutes == null) RadioSessionCommands.CANCEL_SLEEP_TIMER else RadioSessionCommands.SET_SLEEP_TIMER
            val args = Bundle().apply { putInt(RadioSessionCommands.KEY_MINUTES, minutes ?: 0) }
            controller.sendCustomCommand(command, args)
        }

    override fun release() {
        activeController?.release()
        activeController = null
        connected = false
    }

    private fun publish(controller: MediaController) {
        val remaining = controller.sessionExtras.getLong(RadioSessionCommands.KEY_REMAINING_MILLIS, NO_TIMER)
        _snapshot.value =
            RadioPlayerSnapshot(
                stationId = controller.currentMediaItem?.mediaId,
                stationName = controller.mediaMetadata.title?.toString(),
                artworkUri = controller.mediaMetadata.artworkUri,
                status =
                    mapPlaybackState(
                        playbackState = controller.playbackState,
                        isPlaying = controller.isPlaying,
                        hasError = controller.playerError != null,
                    ),
                hasPrevious = controller.hasPreviousMediaItem(),
                hasNext = controller.hasNextMediaItem(),
                sleepTimerRemainingMillis = remaining.takeIf { it != NO_TIMER },
            )
    }

    private fun withController(action: (MediaController) -> Unit) {
        scope.launch {
            action(controllerDeferred.await())
        }
    }

    private companion object {
        const val NO_TIMER = -1L
    }
}
