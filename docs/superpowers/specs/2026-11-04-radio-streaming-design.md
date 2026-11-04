# Radio Streaming Feature Design

**Date:** 2026-11-04
**Status:** Approved by user (scope and visual design). Pending builder check.

## 1. Intent

Add a "Radio Streaming" feature to the Android app. The user opens it from the features list, picks a Spanish radio station from a list, and listens to the live stream from a player screen. Playback continues in the background with a media notification.

Success means: a user can start, pause, stop, and recover from network loss on a live Spanish station, with playback managed by a Media3 service that survives the UI being closed.

## 2. Goals

- Show the 30 most-voted Spanish stations from Radio Browser, ordered by votes.
- Play a station as a live stream with Media3 (`ExoPlayer`, `MediaSessionService`, `MediaController`).
- Player screen with a large station icon, the station name, a "LIVE" indicator, and controls.
- Actions in scope:
  - Play / pause (primary, largest control)
  - Stop (closes the stream)
  - Retry (shown after a connection error)
  - Previous / next station (moves through the loaded list)
  - Sleep timer (stops playback when the chosen time elapses)
- Background playback with a media notification and audio focus handling.
- Everything lives in one feature module: `feature/radio`.

## 3. Non-goals

- Volume control inside the app. Volume uses the device's physical buttons.
- Favorites or any local persistence of stations.
- Sharing a station, showing codec/bitrate in the UI, or searching stations.
- Countries other than Spain. No country selector.
- Pagination. Only the first 30 stations are shown.
- Seek, skip-forward/back, playback speed, and progress bar. These do not apply to live streams.
- Disk caching of the stream. Live streams are unbounded and must not use `SimpleCache`.

## 4. Anti-goals

- Playback must not stop when the screen is locked or the Activity is destroyed while the user is listening.
- Playback must not keep consuming network data after Stop or after the app is closed.
  - Decision: when the app task is removed (swiped from recents) while playback is paused or stopped, the service releases the player and stops. While actively playing, playback continues in the background.
- A network failure must never crash the app or leave the UI showing "playing" when nothing is playing.

## 5. Constraints

- Kotlin 2.4.20, Jetpack Compose with Compose BOM 2026.05.01, Material 3 using the app's existing theme (`shared/core/.../theme/Color.kt`).
- Koin 4.2.2 for DI. Arrow 2.2.3 `Either` for errors. Retrofit for HTTP (as elsewhere in the app).
- Media3 is the playback stack.
- Architecture follows `AGENTS.md`: MVI with `BaseViewModel`, `Either` for errors, Arrow Optics for state updates, `internal` visibility for ViewModels, UI, use cases, and repository implementations.
- Feature modules must not depend on other feature modules.
- Android 13+ requires the `POST_NOTIFICATIONS` runtime permission for the notification.
- Required permissions: `INTERNET`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`.

## 6. Data source

**Endpoint:**
`GET https://de1.api.radio-browser.info/json/stations/bycountry/Spain?limit=30&order=votes&reverse=true`

No API key is needed.

**Fields used:**

| JSON field | Type | Use |
|---|---|---|
| `stationuuid` | String | `MediaItem.mediaId` and the list key |
| `name` | String | Title in the list and the player |
| `url_resolved` | String | Stream URI (`MediaItem.Builder.setUri`) |
| `favicon` | String | Artwork (`MediaMetadata.artworkUri`) |
| `codec` | String | Debug information only |
| `bitrate` | Int | Debug information only |

**Filtering:** Drop stations whose `url_resolved` is empty or whose scheme is not `http` or `https`.

**Cleartext traffic:** Some stations stream over plain `http://`. Their hosts are not known in advance, so a per-domain allow list is not possible. Decision: allow cleartext traffic through a `network_security_config` for the app. This is a security trade-off and is flagged for the user's approval (see section 12).

## 7. Architecture

### 7.1 Module

`feature/radio`, package `com.alxnophis.jetpack.radio`. Layers inside the module:

- **Data (in `feature/radio`):** mapper from DTO to domain, repository implementation, and the rule that drops unplayable stations. The Retrofit service and DTOs come from `shared/api`.
- **Domain:** `Station` model, `RadioError` sealed interface, repository interface (public, per the visibility rules), and use cases such as `GetSpanishStationsUseCase`.
- **Playback:** `PlaybackService` (`MediaSessionService`), the `MediaController` connection, and the sleep timer.
- **UI:** `RadioListViewModel` and `RadioPlayerViewModel` (MVI, extending `BaseViewModel`), and the composables for the list and the player.

### 7.2 Playback service

- `PlaybackService` extends `MediaSessionService`.
- `onCreate()` builds `ExoPlayer` with `setAudioAttributes(..., handleAudioFocus = true)` and a `DefaultLoadControl` with `minBufferMs = 3000` and `bufferForPlaybackMs = 3000`.
- It creates a `MediaSession` for the player.
- `onDestroy()` releases the session and the player.
- No `SimpleCache`.

### 7.3 Connecting the UI

- The ViewModel connects to the service through a `SessionToken` and a `MediaController`.
- A `Player.Listener` maps `STATE_BUFFERING`, `STATE_READY`, `isPlaying`, and `onPlayerError` to the UI state.

### 7.4 Station playback

- Selecting a station builds a `MediaItem` with `mediaId = stationuuid`, `uri = url_resolved`, title = `name`, artist = "Radio Browser - Spain", artwork = `favicon`.
- `LiveConfiguration` uses `setMaxPlaybackSpeed(1.0f)`.
- Then `setMediaItem`, `prepare()`, `play()`.

### 7.5 Previous / next

- When the user selects a station, the full loaded list is set on the `MediaController` as the queue (`setMediaItems`) with the selected station as the start index. Previous and next use the player's own `seekToPreviousMediaItem` / `seekToNextMediaItem`, so the queue lives in the service and survives UI reconnection.
- Decision: at the first or last station, the corresponding button is disabled. No wrap-around.

### 7.6 Sleep timer

- Decision: presets of 15, 30, and 60 minutes plus "Off".
- The timer is owned by `PlaybackService`, so it keeps working if the UI is closed.
- When it expires, playback stops (same as Stop).
- The current timer and remaining time are reported to the UI.

## 8. UI

### 8.1 Entry point

- "Radio Streaming" is added to the features list. Because feature modules must not depend on each other, the entry and its navigation route are wired in `:app`.

### 8.2 Station list screen

- Top bar in the app's Material 3 style, title "Radio Streaming", back navigation.
- Vertical list of cards. Each card shows the favicon (or a placeholder), the station name, and codec/bitrate in small text.
- Loading state, empty state, and error state with a retry action.

### 8.3 Player screen

- Top bar with the back arrow and the station name.
- Large station icon (200 dp, rounded corners) with the station name below it.
- "● EN VIVO" chip.
- Control row: previous (52 dp), play/pause (84 dp, primary color, the largest control), next (52 dp).
- Secondary row: stop (44 dp) and sleep timer (44 dp).
- Buffering: play/pause shows a progress indicator.
- Error state: "Sin conexión" chip, short message, and a "Reintentar" button.

Approved mockups: `.superpowers/brainstorm/7668-1793828251/content/player-final.html`.

## 9. Errors

Domain errors are a sealed interface `RadioError`:

- `NetworkUnavailable`: no connection when loading the list.
- `ServerError`: the API returned an error or an unexpected response.
- `StreamUnavailable`: the playback error from ExoPlayer.

Repository and use cases return `Either<RadioError, T>`. Exceptions are not used for business logic.

## 10. Testing

- Unit tests for the mapper and for the filter that drops stations with empty or non-HTTP URLs.
- Unit tests for the repository with a fake API service, covering success and each `RadioError`.
- ViewModel tests with Turbine, following the `GIVEN ... WHEN ... THEN ...` naming.
- Playback state mapping tested with a fake `Player` or by testing the mapper function that converts player state to UI state.
- Sleep timer tested with a controllable clock or test dispatcher.
- Screenshot tests (Roborazzi) for the list and the player, including the error state.
- `ktlintCheck` and `lintDebug` must pass.

## 11. Risks

- Radio Browser mirror is down: the list fails. Only `de1` is used; mirror fallback is out of scope and can be added later.
- Stations over HTTP can be blocked or insecure. Accepted through the cleartext decision.
- A 3-second buffer with 320 kbps streams can stutter on weak networks. Accepted by the user; can be raised later.
- Notification permission denied on Android 13+: playback still works but no notification appears. The user is informed in the UI.

## 12. Decisions made by the designer (to confirm)

1. Cleartext traffic allowed app-wide through `network_security_config`, since hosts are dynamic. Note: this is broader than the earlier per-station idea and is a security trade-off.
2. Previous/next do not wrap around at the ends of the list.
3. Sleep timer presets: 15, 30, 60 minutes, plus Off. Owned by `PlaybackService`.
4. Previous/next and sleep timer use the same playback service as Play/Stop.
5. The Radio Browser API client (Retrofit service, DTOs, factory, and DI module) lives in `shared/api` under `com.alxnophis.jetpack.api.radiobrowser`, following the existing `themoviedb` and `jsonplaceholder` structure. Everything else (mapping, repository, UI, playback) stays in `feature/radio`.
6. Radio Browser mirror: only `de1`, no fallback.
7. Buffer: `minBufferMs = 3000`, `bufferForPlaybackMs = 3000`, other `DefaultLoadControl` values left at their defaults.
8. Player screen and list screen use the same app theme and support light and dark mode.
9. Recovery from network loss is manual: the user taps "Reintentar". No automatic reconnect policy.
10. The playlist is stored as the `MediaController` queue in `PlaybackService`, not in UI state (see 7.5).

## 13. Open items for the builder

None that change what gets built. Items not listed above are decided by the implementer following the codebase's existing patterns.

## 14. Builder check log

- Cleartext allowance (12.1): pending user approval.
- Task removal behavior: decided (see Anti-goals).
- Recovery: decided as manual (12.9).
- Playlist ownership: decided as service queue (12.10).
