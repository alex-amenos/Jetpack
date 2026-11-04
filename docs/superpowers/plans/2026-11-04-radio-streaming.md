# Radio Streaming Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a "Radio Streaming" feature that lists the 50 most-voted Spanish stations from Radio Browser and plays a selected station as a live stream with Media3, with background playback, notification, previous/next, sleep timer, and manual retry.

**Architecture:** One new module `feature/radio` (package `com.alxnophis.jetpack.radio`). A Media3 `MediaSessionService` owns `ExoPlayer`, the queue, and the sleep timer. The UI connects through a `MediaController` from MVI ViewModels. Data comes from Radio Browser via Retrofit and is mapped to a domain `Station` model with `Either<RadioError, T>`. The entry point is wired in `:app`.

**Tech Stack:** Kotlin 2.4.20, Jetpack Compose (BOM 2026.05.01), Material 3, Media3 (ExoPlayer, session, session service), Koin 4.2.2, Arrow 2.2.3, Retrofit, Kotest, Turbine, Roborazzi.

**Spec:** `docs/superpowers/specs/2026-11-04-radio-streaming-design.md`

## Global Constraints

- Kotlin 2.4.20; Compose BOM 2026.05.01; Koin 4.2.2; Arrow 2.2.3.
- Only Spanish stations: `GET https://de1.api.radio-browser.info/json/stations/bycountry/Spain?limit=100&order=votes&reverse=true`.
- Buffer: `DefaultLoadControl` with `minBufferMs = 3000` and `bufferForPlaybackMs = 3000`.
- No `SimpleCache` for the stream.
- Required permissions: `INTERNET`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, `POST_NOTIFICATIONS` (runtime, Android 13+).
- Cleartext HTTP is not allowed; only `https` stream URLs are played (`http://` stations are filtered out).
- ViewModels, UI, use cases, and repository implementations are `internal`. Repository interface is internal too (the feature exposes no cross-module API).
- Imports: no wildcards; order Android, third-party, project.
- Test names: ``GIVEN ... WHEN ... THEN ...``.
- Feature modules must not depend on other feature modules.

## Review Focus

1. **Station with empty or non-HTTPS `url_resolved`:** must be dropped from the list, not shown. Pinned by Task 3.
2. **API returns zero stations:** the list shows the empty state, not an error. Pinned by Task 3 and Task 7.
3. **Network loss during playback:** the UI shows the error state with "Reintentar" and never shows "playing". Pinned by Task 6.
4. **Retry tapped after error:** playback restarts on the same station without re-selecting it. Pinned by Task 6.
5. **Previous/next at ends of queue:** the disabled state is correct and calling the action does nothing. Pinned by Task 6.

---

### Task 1: Module scaffolding and Media3 dependencies

**Files:**
- Create: `feature/radio/build.gradle`
- Create: `feature/radio/src/main/AndroidManifest.xml`
- Modify: `settings.gradle` (add `include ':feature:radio'`)
- Modify: `gradle/libs.versions.toml` (add `media3` version and libraries: `androidx-media3-exoplayer`, `androidx-media3-session`, `androidx-media3-ui-compose` if needed, `androidx-media3-test-utils` for tests)
- Modify: `feature/root/build.gradle` (add `implementation project(Modules.FEATURE_RADIO)`), `buildSrc/src/main/java/Modules.kt` (add `FEATURE_RADIO`)

**Interfaces:**
- Consumes: none
- Produces: module `:feature:radio` with namespace `com.alxnophis.jetpack.radio`, and Media3 libraries available to it.

- [ ] **Step 1: Copy `feature/home/build.gradle` pattern**

Create `feature/radio/build.gradle` with `apply from: "$rootDir/buildSystem/gradle/common-android-feature.gradle"`, an `android { namespace = 'com.alxnophis.jetpack.radio' }` block, and `dependencies` adding `implementation project(Modules.SHARED_API)`, `implementation project(Modules.SHARED_CORE)`, Retrofit, Koin, Arrow, Media3 libraries. Use the same `resourcePrefix` convention (`radio`).

- [ ] **Step 2: Register the module**

Add `include ':feature:radio'` under "Feature modules" in `settings.gradle`.

- [ ] **Step 3: Add the Media3 version and libraries**

Add a `media3` version to `gradle/libs.versions.toml` and the library entries listed above.

- [ ] **Step 4: Declare the manifest with permissions and the service**

In `feature/radio/src/main/AndroidManifest.xml`, declare the permissions from Global Constraints and `<service android:name=".playback.PlaybackService" android:foregroundServiceType="mediaPlayback" android:exported="false"> <intent-filter><action android:name="androidx.media3.session.MediaSessionService"/></intent-filter></service>`. Do not add a `networkSecurityConfig` (cleartext stays blocked).

- [ ] **Step 5: Verify the build**

Run: `./gradlew :feature:radio:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git add settings.gradle gradle/libs.versions.toml app/build.gradle feature/radio
git commit -m "build: add feature radio module with media3"
```

---

### Task 2: Domain model and error type

**Files:**
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/domain/model/Station.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/domain/model/RadioError.kt`
- Test: `feature/radio/src/test/kotlin/com/alxnophis/jetpack/radio/domain/model/StationMother.kt`

**Interfaces:**
- Produces:
  - `data class Station(val id: String, val name: String, val streamUrl: String, val faviconUrl: String?, val codec: String?, val bitrate: Int)`
  - `sealed interface RadioError { data object NetworkUnavailable; data object ServerError; data object StreamUnavailable }`
  - `object StationMother` with `fun create(id: String = "1", name: String = "Cadena SER", streamUrl: String = "https://example.com/ser.mp3", ...)`

- [ ] **Step 1: Create the model and error types** as specified above, `internal`.
- [ ] **Step 2: Create `StationMother`** for tests.
- [ ] **Step 3: Verify compilation**

Run: `./gradlew :feature:radio:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add feature/radio/src/main/java/com/alxnophis/jetpack/radio/domain feature/radio/src/test
git commit -m "feat(radio): add station domain model and errors"
```

---

### Task 3: Remote data source, DTO mapper, and filter

**Files:**
- Create: `shared/api/src/main/java/com/alxnophis/jetpack/api/radiobrowser/RadioBrowserRetrofitFactory.kt`
- Create: `shared/api/src/main/java/com/alxnophis/jetpack/api/radiobrowser/RadioBrowserServices.kt`
- Create: `shared/api/src/main/java/com/alxnophis/jetpack/api/radiobrowser/model/StationApiModel.kt`
- Create: `shared/api/src/main/java/com/alxnophis/jetpack/api/radiobrowser/di/RadioBrowserModule.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/data/mapper/StationMapper.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/data/repository/StationsRepositoryImpl.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/domain/repository/StationsRepository.kt`
- Test: `feature/radio/src/test/kotlin/com/alxnophis/jetpack/radio/data/mapper/StationMapperTest.kt`
- Test: `feature/radio/src/test/kotlin/com/alxnophis/jetpack/radio/data/repository/StationsRepositoryImplTest.kt`

**Interfaces:**
- Produces:
  - In `shared/api`: `interface RadioBrowserServices { @GET("json/stations/bycountry/Spain") suspend fun getSpanishStations(@Query("limit") limit: Int = 30, @Query("order") order: String = "votes", @Query("reverse") reverse: Boolean = true): List<StationApiModel> }`, `data class StationApiModel(stationuuid, name, url_resolved, favicon, codec, bitrate)` with `@SerialName` where needed, and `RadioBrowserModule` providing the service through the Retrofit factory (base URL `https://de1.api.radio-browser.info/`), following `themoviedb`.
  - `fun StationApiModel.toStationOrNull(): Station?` in `feature/radio`, returns null when `url_resolved` is blank or scheme is not `https`.
  - `interface StationsRepository { suspend fun getSpanishStations(): Either<RadioError, List<Station>> }`
  - `class StationsRepositoryImpl(api: RadioBrowserApi) : StationsRepository`, maps `IOException` to `NetworkUnavailable`, HTTP and other exceptions to `ServerError`.

- [ ] **Step 1: Write the failing mapper test**

Test names: `GIVEN a station with blank url_resolved WHEN mapped THEN returns null` and `GIVEN a station with ftp url WHEN mapped THEN returns null` and `GIVEN a station with https url WHEN mapped THEN returns Station with same id, name, streamUrl`.

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :feature:radio:testDebugUnitTest --tests "*.StationMapperTest"`
Expected: FAIL (mapper not defined)

- [ ] **Step 3: Implement the DTO and `toStationOrNull`**

- [ ] **Step 4: Run mapper tests to verify they pass**

Run: same command. Expected: PASS

- [ ] **Step 5: Write failing repository tests**

Test names: `GIVEN the API returns stations with valid and invalid URLs WHEN loading THEN only valid stations are returned`, `GIVEN a station with an http URL WHEN mapped THEN returns null`, `GIVEN the API returns an empty list WHEN loading THEN returns an empty right`, `GIVEN an IOException WHEN loading THEN returns NetworkUnavailable`, `GIVEN an HTTP error WHEN loading THEN returns ServerError`.

- [ ] **Step 6: Run to verify they fail**

Run: `./gradlew :feature:radio:testDebugUnitTest --tests "*.StationsRepositoryImplTest"`
Expected: FAIL

- [ ] **Step 7: Implement the repository**

The Retrofit service (base URL `https://de1.api.radio-browser.info/`) is provided by `RadioBrowserModule` in `shared/api`; Task 9 wires the repository into Koin.

- [ ] **Step 8: Run tests to verify they pass**

Run: same command. Expected: PASS

- [ ] **Step 9: Commit**

```bash
git add feature/radio/src
git commit -m "feat(radio): load and filter spanish stations"
```

---

### Task 4: Playback service with ExoPlayer, session, and load control

**Files:**
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/playback/PlaybackService.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/playback/RadioLoadControl.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/playback/SleepTimer.kt`
- Test: `feature/radio/src/test/kotlin/com/alxnophis/jetpack/radio/playback/SleepTimerTest.kt`

**Interfaces:**
- Produces:
  - `object RadioLoadControl { fun create(): DefaultLoadControl }` with `minBufferMs = 3000`, `bufferForPlaybackMs = 3000`, `maxBufferMs` equal to Media3 default.
  - `class SleepTimer(scope: CoroutineScope, onExpired: () -> Unit) { fun start(minutes: Int); fun cancel(); val remainingMillis: StateFlow<Long?> }` (a null value means no timer).
  - `class PlaybackService : MediaSessionService` with `onCreate` building `ExoPlayer` (`setAudioAttributes(AudioAttributes, handleAudioFocus = true)`, `setLoadControl(RadioLoadControl.create())`), `MediaSession`, and `onTaskRemoved` releasing when not playing; `onDestroy` releasing session and player.
  - Custom session command `SET_SLEEP_TIMER` with an int argument in minutes and `CANCEL_SLEEP_TIMER`.

- [ ] **Step 1: Write the failing SleepTimer test**

Test names: `GIVEN a 15 minute timer WHEN time elapses THEN onExpired is called once`, `GIVEN an active timer WHEN cancelled THEN onExpired is never called and remainingMillis is null`.

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :feature:radio:testDebugUnitTest --tests "*.SleepTimerTest"`
Expected: FAIL

- [ ] **Step 3: Implement `SleepTimer` with a coroutine delay and a ticking `remainingMillis`**

- [ ] **Step 4: Run to verify it passes**

Run: same. Expected: PASS

- [ ] **Step 5: Implement `RadioLoadControl` and `PlaybackService`**

Service must set the sleep timer's `onExpired` to call `player.stop()`. Service must handle the custom session commands from Step above.

- [ ] **Step 6: Verify compilation and manifest merge**

Run: `./gradlew :feature:radio:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 7: Commit**

```bash
git add feature/radio/src
git commit -m "feat(radio): add playback service with sleep timer"
```

---

### Task 5: Use cases and player controller

**Files:**
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/domain/usecase/GetSpanishStationsUseCase.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/playback/RadioPlayerController.kt`
- Test: `feature/radio/src/test/kotlin/com/alxnophis/jetpack/radio/domain/usecase/GetSpanishStationsUseCaseTest.kt`

**Interfaces:**
- Consumes: `StationsRepository` (Task 3), `SleepTimer` commands (Task 4).
- Produces:
  - `internal class GetSpanishStationsUseCase(repository: StationsRepository) { suspend operator fun invoke(): Either<RadioError, List<Station>> }`
  - `internal class RadioPlayerController(context: Context) { fun connect(); fun release(); fun playQueue(stations: List<Station>, startIndex: Int); fun play(); fun pause(); fun stop(); fun next(); fun previous(); fun retry(); fun setSleepTimer(minutes: Int?); val state: StateFlow<PlayerUiState> }` where `PlayerUiState` is defined in Task 6.

- [ ] **Step 1: Write failing use case test** with names `GIVEN repository returns stations WHEN invoked THEN returns them` and `GIVEN repository fails WHEN invoked THEN propagates the error`.
- [ ] **Step 2: Run to verify it fails.** Run: `./gradlew :feature:radio:testDebugUnitTest --tests "*.GetSpanishStationsUseCaseTest"`. Expected: FAIL
- [ ] **Step 3: Implement use case.** Run same command. Expected: PASS
- [ ] **Step 4: Implement `RadioPlayerController`** using `MediaController.Builder(context, SessionToken(context, ComponentName(context, PlaybackService::class.java)))`. `playQueue` maps each `Station` to a `MediaItem` (`setMediaId(id)`, `setUri(streamUrl)`, `MediaMetadata` title = name, artist = "Radio Browser - Spain", artworkUri = faviconUrl, `LiveConfiguration` with `setMaxPlaybackSpeed(1.0f)`), then `setMediaItems(items, startIndex, 0L)`, `prepare()`, `play()`. `retry()` calls `prepare()` and `play()`. `previous()`/`next()` call `seekToPreviousMediaItem()`/`seekToNextMediaItem()`. `stop()` calls `stop()` on the controller. Listener maps player events into `state`.
- [ ] **Step 5: Verify compilation.** Run: `./gradlew :feature:radio:compileDebugKotlin`. Expected: BUILD SUCCESSFUL
- [ ] **Step 6: Commit**

```bash
git add feature/radio/src
git commit -m "feat(radio): add use case and player controller"
```

---

### Task 6: Player state mapping and player ViewModel

**Files:**
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/ui/contract/RadioPlayerContract.kt` (state, actions, events)
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/ui/viewmodel/RadioPlayerViewModel.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/ui/mapper/PlayerStateMapper.kt`
- Test: `feature/radio/src/test/kotlin/com/alxnophis/jetpack/radio/ui/viewmodel/RadioPlayerViewModelTest.kt`
- Test: `feature/radio/src/test/kotlin/com/alxnophis/jetpack/radio/ui/mapper/PlayerStateMapperTest.kt`

**Interfaces:**
- Consumes: `RadioPlayerController` (Task 5).
- Produces:
  - `internal data class PlayerUiState(val station: Station?, val status: PlaybackStatus, val canPrevious: Boolean, val canNext: Boolean, val sleepTimerMinutes: Int?, val remainingMillis: Long?)`
  - `internal sealed interface PlaybackStatus { data object Idle; data object Buffering; data object Playing; data object Paused; data object Error }` (Error corresponds to `RadioError.StreamUnavailable`)
  - `internal sealed interface RadioPlayerAction { Play; Pause; Stop; Next; Previous; Retry; SetSleepTimer(minutes: Int?) }`
  - `internal fun Player.toPlaybackStatus(): PlaybackStatus` (mapper, pure function on player state values; test via a function `fun mapPlaybackState(playbackState: Int, isPlaying: Boolean, hasError: Boolean): PlaybackStatus`)
  - `internal class RadioPlayerViewModel(...) : BaseViewModel<RadioPlayerAction, PlayerUiState>`

- [ ] **Step 1: Write failing mapper tests**: `GIVEN buffering state WHEN mapped THEN Buffering`, `GIVEN ready and playing WHEN mapped THEN Playing`, `GIVEN ready and not playing WHEN mapped THEN Paused`, `GIVEN error WHEN mapped THEN Error`.
- [ ] **Step 2: Run to verify fail.** Run: `./gradlew :feature:radio:testDebugUnitTest --tests "*.PlayerStateMapperTest"`. Expected: FAIL
- [ ] **Step 3: Implement mapper.** Run same. Expected: PASS
- [ ] **Step 4: Write failing ViewModel tests** with Turbine: `GIVEN first station WHEN previous is available THEN canPrevious is false`, `GIVEN last station WHEN next is available THEN canNext is false`, `GIVEN error status WHEN Retry is handled THEN controller.retry is called`, `GIVEN Stop WHEN handled THEN controller.stop is called and status becomes Idle`.
- [ ] **Step 5: Run to verify fail.** Run: `./gradlew :feature:radio:testDebugUnitTest --tests "*.RadioPlayerViewModelTest"`. Expected: FAIL
- [ ] **Step 6: Implement ViewModel** extending `BaseViewModel`, updating state with Arrow Optics `updateCopy` (`PlayerUiState.status set ...`), mapping controller state.
- [ ] **Step 7: Run to verify pass.** Run same. Expected: PASS
- [ ] **Step 8: Commit**

```bash
git add feature/radio/src
git commit -m "feat(radio): add player state and view model"
```

---

### Task 7: Station list ViewModel and screen

**Files:**
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/ui/contract/StationListContract.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/ui/viewmodel/StationListViewModel.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/ui/composable/StationListScreen.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/ui/composable/StationListRoot.kt` (stateful root)
- Test: `feature/radio/src/test/kotlin/com/alxnophis/jetpack/radio/ui/viewmodel/StationListViewModelTest.kt`
- Test: `feature/radio/src/test/kotlin/com/alxnophis/jetpack/radio/ui/composable/StationListScreenshotTest.kt`

**Interfaces:**
- Consumes: `GetSpanishStationsUseCase` (Task 5).
- Produces:
  - `internal data class StationListUiState(val status: ListStatus, val stations: ImmutableList<Station>)`, `ListStatus { Loading, Content, Empty, Error }`
  - `internal sealed interface StationListAction { Load; Retry; Select(station: Station) }`
  - `StationListViewModel`: on `Select`, emits navigation event `Event.OpenPlayer(index)`.
  - `@Composable internal fun StationListScreen(state: StationListUiState, onAction: (StationListAction) -> Unit)`

- [ ] **Step 1: Write failing ViewModel tests** with names `GIVEN use case returns stations WHEN loaded THEN status is Content`, `GIVEN use case returns empty list WHEN loaded THEN status is Empty`, `GIVEN use case returns NetworkUnavailable WHEN loaded THEN status is Error`, `GIVEN Retry WHEN Error state THEN load runs again`.
- [ ] **Step 2: Run to verify fail.** Run: `./gradlew :feature:radio:testDebugUnitTest --tests "*.StationListViewModelTest"`. Expected: FAIL
- [ ] **Step 3: Implement ViewModel.** Run same. Expected: PASS
- [ ] **Step 4: Implement `StationListScreen`** with a `LazyColumn` of Material 3 cards (favicon via Coil or placeholder emoji box, name, codec/bitrate small text). Loading uses `CircularProgressIndicator`, Empty and Error show text and a Retry button. Top bar uses `TopAppBar` with `primary` container and `onPrimary` content, as in `HomeScreen`.
- [ ] **Step 5: Add screenshot test** `StationListScreenshotTest` for Content and Error states using Roborazzi, following `PostScreenshotTest` in `feature/posts`.
- [ ] **Step 6: Record and verify screenshots**

Run: `./gradlew :feature:radio:recordRoborazziDebug`
Then: `./gradlew :feature:radio:verifyRoborazziDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 7: Commit**

```bash
git add feature/radio/src
git commit -m "feat(radio): add station list screen"
```

---

### Task 8: Player screen

**Files:**
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/ui/composable/RadioPlayerScreen.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/ui/composable/RadioPlayerRoot.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/ui/composable/SleepTimerSheet.kt`
- Test: `feature/radio/src/test/kotlin/com/alxnophis/jetpack/radio/ui/composable/RadioPlayerScreenshotTest.kt`

**Interfaces:**
- Consumes: `PlayerUiState`, `RadioPlayerAction` (Task 6).
- Produces: `@Composable internal fun RadioPlayerScreen(state: PlayerUiState, onAction: (RadioPlayerAction) -> Unit)`.

- [ ] **Step 1: Implement the screen** exactly as the approved mockup `player-final.html`: 200 dp icon with rounded corners, name, "● EN VIVO" chip, control row (previous 52 dp, play/pause 84 dp primary, next 52 dp), secondary row (stop 44 dp, timer 44 dp that opens `SleepTimerSheet` with 15/30/60/Off), buffering shows `CircularProgressIndicator` inside the play button, error shows "Sin conexión" chip, message, and "Reintentar" button. Previous/next are disabled when `canPrevious`/`canNext` is false.
- [ ] **Step 2: Add screenshot tests** for Playing, Buffering, and Error states with Roborazzi.
- [ ] **Step 3: Record and verify**

Run: `./gradlew :feature:radio:recordRoborazziDebug` then `./gradlew :feature:radio:verifyRoborazziDebug`. Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add feature/radio/src
git commit -m "feat(radio): add player screen"
```

---

### Task 9: Koin wiring, navigation, and notification permission

**Files:**
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/di/RadioModule.kt`
- Create: `feature/radio/src/main/java/com/alxnophis/jetpack/radio/ui/navigation/RadioNavGraph.kt`
- Modify: `app/src/main/.../` (the module that hosts the features list and nav graph; locate with `grep -rn "NavGraphBuilder\|composable<" app/src` before editing) to add the "Radio Streaming" entry and route
- Test: `feature/radio/src/test/kotlin/com/alxnophis/jetpack/radio/di/RadioModuleTest.kt`

**Interfaces:**
- Consumes: all previous tasks.
- Produces: `val radioModule: Module` providing `StationsRepository`, `GetSpanishStationsUseCase`, `RadioPlayerController`, `StationListViewModel`, `RadioPlayerViewModel`. The Retrofit `RadioBrowserServices` comes from `RadioBrowserModule` in `shared/api`. `fun NavGraphBuilder.radioGraph()` with routes `radio/stations` and `radio/player`.

- [ ] **Step 1: Write failing Koin verification test** using `koin-test` `verify()`, named `GIVEN radioModule WHEN verified THEN all definitions resolve`.
- [ ] **Step 2: Run to verify fail.** Run: `./gradlew :feature:radio:testDebugUnitTest --tests "*.RadioModuleTest"`. Expected: FAIL
- [ ] **Step 3: Implement `radioModule` and `radioGraph`.** Run same. Expected: PASS
- [ ] **Step 4: Wire the entry in `:app`.** Add "Radio Streaming" to the features list and register `radioGraph()`. Register `radioModule` in the Koin start list.
- [ ] **Step 5: Request `POST_NOTIFICATIONS`** on Android 13+ when the player screen first opens, using the same permission pattern as `feature/notifications` if present; otherwise use `rememberLauncherForActivityResult(RequestPermission())`. Playback still starts if denied.
- [ ] **Step 6: Verify the app builds**

Run: `./gradlew :app:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 7: Commit**

```bash
git add feature/radio/src app/src
git commit -m "feat(radio): wire koin, navigation and entry point"
```

---

### Task 10: Final verification

- [ ] **Step 1: Run ktlint**

Run: `./gradlew ktlintCheck`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Run lint**

Run: `./gradlew :feature:radio:lintDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Run all radio tests and screenshot verification**

Run: `./gradlew :feature:radio:testDebugUnitTest :feature:radio:verifyRoborazziDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Manual check on device or emulator**

Verify: list loads 30 Spanish stations; play, pause, stop, retry, previous, next, and sleep timer work; notification appears; closing the app while paused releases the stream (no network in logcat).

- [ ] **Step 5: Do not commit or push without the user's review**, per `AGENTS.md`.
