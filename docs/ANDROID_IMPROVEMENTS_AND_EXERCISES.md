# Modern Android Improvements, Exercises, and Architectural Playgrounds

## 1. Executive Summary & Project Purpose

The **Jetpack** project is a multi-module Android application designed as an experimental engineering playground and sandbox. Its primary mission is to evaluate, adopt, and benchmark modern Android development paradigms, emerging Jetpack libraries, functional programming patterns, and robust testing strategies under production-like conditions.

Rather than remaining a theoretical showcase, the project treats architectural evolutions as tracked **Exercises** and **Technical Spikes** (cataloged via repository issues). Each experiment tackles a real-world software engineering challenge—such as migrating to declarative navigation architectures, enforcing functional purity in unidirectional data flows, or establishing rock-solid visual regression testing.

### Key Architectural Pillars
- **Strict Multi-Module Separation:** Vertical feature slicing (`:feature:*`) isolated from shared horizontal infrastructure (`:shared:*`) and application wiring (`:app`).
- **Unidirectional Data Flow (MVI):** Deterministic state progression managed through `BaseViewModel<Event, State>` with immutable data structures.
- **Functional Programming with Arrow-kt:** Sealed domain error hierarchies, typed failure handling with `Either<DomainError, Success>`, and declarative immutable state transformations using `@optics`.
- **Adaptive Jetpack Compose UI:** Universal multi-form-factor support spanning compact phones, foldables, and large tablet displays using Jetpack Navigation 3 and `ListDetailSceneStrategy`.
- **Offline-First Synchronization:** Local persistence acting as the Single Source of Truth (SSOT) via Room database caching paired with network fallbacks.
- **Hermetic & Visual Regression Testing:** Comprehensive unit test suites (JUnit 5, Kotest assertions, Turbine) accompanied by Roborazzi screenshot baselines executed on Robolectric.

---

## 2. Core Architecture & Architectural Patterns

### 2.1 Multi-Module Project Structure

The codebase is organized into three distinct tiers of Gradle modules to enforce dependency encapsulation, prevent circular coupling, and optimize incremental compilation times:

```
                      ┌───────────────┐
                      │     :app      │ (Application Shell & Koin Init)
                      └───────┬───────┘
                              │
                              ▼
                      ┌───────────────┐
                      │ :feature:root │ (Navigation Host & Aggregator)
                      └──┬─────────┬──┘
                         │         │
       ┌─────────────────┼─────────┴─────────────────┐
       ▼                 ▼                           ▼
┌──────────────┐  ┌──────────────┐            ┌──────────────┐
│:feature:posts│  │:feature:movies│ ...        │:feature:my-..│ (Vertical Feature Slices)
└──────┬───────┘  └──────┬───────┘            └──────┬───────┘
       │                 │                           │
       └─────────────────┼───────────────────────────┘
                         │
                         ▼
┌────────────────────────────────────────────────────────────┐
│                       :shared:*                            │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌──────────┐ │
│  │:shared:core│ │:shared:api │ │:shared:kt  │ │:shared:ts│ │
│  └────────────┘ └────────────┘ └────────────┘ └──────────┘ │
└────────────────────────────────────────────────────────────┘
```

1. **Application Shell (`:app`):** Contains `JetpackApp` (`Application` subclass) and root dependency injection bootstrap. Holds no business logic.
2. **Aggregator & Composition (`:feature:root`):** Connects the root navigation graph (`Navigation.kt`) and hosts `RootActivity`. It depends on each individual feature module to render their composables while keeping leaf features completely decoupled from one another.
3. **Vertical Feature Slices (`:feature:*`):** Independent modules representing self-contained product domains (e.g., `:feature:posts`, `:feature:movies`, `:feature:location-tracker`, `:feature:settings`, `:feature:authentication`, `:feature:file-downloader`, `:feature:notifications`, `:feature:game:ballclicker`, `:feature:my-playground`). Feature modules **never** depend on peer feature modules.
4. **Horizontal Shared Foundations (`:shared:*`):** Cross-cutting infrastructure libraries:
   - `:shared:core`: Base classes (`BaseViewModel`), UI theme (`AppTheme`), common composables (`CoreTopBar`), cryptography utilities.
   - `:shared:api`: Retrofit client factories, OkHttp interceptors, and network adapters for external services (TMDB, JSONPlaceholder).
   - `:shared:kotlin`: Pure Kotlin utility extensions and constants.
   - `:shared:testing`: Base test classes (`BaseUnitTest`, `BaseViewModelUnitTest`), test dispatchers, and testing helpers.

### 2.2 Model-View-Intent (MVI) Architecture

All interactive screens follow the Model-View-Intent pattern to guarantee predictable and reproducible state transitions.

#### The `BaseViewModel` Foundation
Each feature ViewModel extends `BaseViewModel<Event, State>` from `:shared:core`:
```kotlin
abstract class BaseViewModel<Event, State>(initialState: State) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    abstract fun handleEvent(event: Event)

    protected fun setUiState(update: State.() -> State) {
        _uiState.update { it.update() }
    }
}
```

#### Contract Definition
Every feature defines a strongly typed contract sealed interface hierarchy encapsulating all UI interactions and states:
- **`Event`:** User actions, system triggers, or navigation requests (e.g., `MoviesEvent.SearchQueryChanged`, `PostsEvent.RefreshTriggered`).
- **`State`:** A single immutable data class representing the complete screen state at any instant.
- **`Effect` / `Navigation`:** One-off side effects handled through event callbacks or dedicated state flows.

### 2.3 Functional Programming with Arrow-kt

The project integrates Arrow to bring functional programming best practices to Kotlin:

1. **Typed Domain Errors with `Either`:**
   Instead of throwing exceptions across architectural boundaries, repositories and use cases return `Either<DomainError, Data>`. Domain errors are structured as sealed interfaces:
   ```kotlin
   sealed interface PostsError {
       data object NetworkFailure : PostsError
       data object ServerError : PostsError
       data class Unknown(val throwable: Throwable) : PostsError
   }
   ```
   Consumers handle outcomes explicitly with `.fold(ifLeft = { ... }, ifRight = { ... })` or `.mapLeft { ... }`, making error branches compile-time enforced.

2. **Immutable State Manipulation with Arrow Optics:**
   Complex UI states often suffer from deeply nested `copy()` cascades. By annotating UI state models with `@optics`, ViewModels leverage optics DSLs for clean, concise, and type-safe updates:
   ```kotlin
   _uiState.updateCopy {
       PostsUiState.status set PostsStatus.Loaded
       PostsUiState.posts set newPostsList
   }
   ```

### 2.4 Modern Jetpack Compose UI & Adaptive Layouts

- **Edge-to-Edge by Default:** Screens leverage `WindowInsets.safeDrawing` and Scaffold padding to draw under system navigation and status bars natively.
- **Adaptive Multi-Pane Navigation:** Powered by Jetpack Navigation 3 and `androidx.compose.material3.adaptive:adaptive-navigation3`.
  The `:feature:movies` and `:feature:posts` modules employ `ListDetailSceneStrategy` to render side-by-side list/detail panes on foldable and tablet viewports, while falling back gracefully to single-column sequential navigation on compact phones.
- **Material 3 Design System:** Universal theme token system supporting system-driven Dynamic Color, light/dark modes, and unified typography.

---

## 3. Completed Exercises & Technical Improvements

The repository's development history is driven by concrete spikes and exercises. Below is a comprehensive breakdown of the major technical advancements achieved.

### 3.1 NetworkBoundResource Pattern in `:feature:posts` (Issue #140)
- **Challenge:** Avoid UI flickering, ensure instantaneous screen population on app start, and support offline consumption while synchronizing with remote REST endpoints.
- **Solution:** Implemented the Single Source of Truth (SSOT) architecture using Room database caching.
- **Implementation:**
  - Room DAO provides `Flow<List<PostEntity>>` representing the persistent local state.
  - The repository emits cached data immediately to the UI layer.
  - Concurrently, a network request to JSONPlaceholder is dispatched via Retrofit.
  - Successful network responses are mapped to domain models, persisted into Room, and automatically reflected in the active UI flow.
  - If the network fails, cached data remains visible, and the UI displays a non-intrusive warning rather than a blank error screen.
  - Integrated with WorkManager for background sync every 24 hours.

### 3.2 Navigation 3 & Adaptive Layouts Migration (Issues #137, #107, #110)
- **Challenge:** Legacy Navigation Component relied on string routes and XML-based navigation graphs, making type safety fragile and adaptive two-pane layouts cumbersome.
- **Solution:** Migrated root navigation to Jetpack Navigation 3 with type-safe destination objects (`Route.kt`) and Material 3 adaptive layout scaffolds.
- **Implementation:**
  - Type-safe routes modeled as `@Serializable` objects and data classes.
  - `ListDetailSceneStrategy` dynamically evaluates window size classes (Compact, Medium, Expanded).
  - On compact devices, tapping a list item pushes the detail view onto the back stack.
  - On expanded screens (tablets, Chromebooks, foldables unfolded), list and detail panes display side-by-side without duplicating composable state.

### 3.3 Screenshot Testing Migration to Roborazzi (Issues #144, #113)
- **Challenge:** Early screenshot testing solutions required physical devices or emulators, making CI execution slow and prone to timeout flakes.
- **Solution:** Replaced screenshot tests with **Roborazzi** combined with Robolectric.
- **Implementation:**
  - Fast JVM-based rendering without needing connected Android hardware or emulators.
  - Configured via Gradle tasks: `./gradlew verifyRoborazziDebug` (verification) and `./gradlew recordRoborazziDebug` (golden baseline generation).
  - Tests verify both Light and Dark themes, multiple display sizes, and accessibility text scaling.

### 3.4 Kotlin 2.4 & K2 Compiler Migration (Issue #103 & PR #210)
- **Challenge:** Adopting the modern Kotlin compiler architecture (K2) to improve compile performance and unlock modern language capabilities.
- **Solution:** Upgraded project to Kotlin 2.4.20 with the Compose Compiler plugin bundled natively.
- **Implementation:**
  - Enabled support for context parameters and explicit backing fields.
  - Configured `.editorconfig` to support uppercase BDD test naming (`ktlint_function_naming_ignore_when_annotated_with = Composable,Test`).
  - Aligned Gradle 9.7 and Android Gradle Plugin 8.9.

### 3.5 Material Design 3 Migration (Issue #77)
- **Challenge:** Modernizing legacy Material 2 theming, colors, and components.
- **Solution:** Fully migrated to Material 3 (`androidx.compose.material3`).
- **Implementation:**
  - Implemented dynamic color schemes adapting to Android 12+ wallpaper palettes.
  - Standardized unified spacing and paddings (`mediumPadding`, `smallPadding`) in `:shared:core`.
  - Replaced legacy TopAppBars and Buttons with M3 equivalents (`TopAppBarDefaults`, `FilledTonalButton`).

### 3.6 Centralized Gradle Version Catalogs (Issue #57)
- **Challenge:** Dependency version drift across multi-module builds.
- **Solution:** Centralized all versions, libraries, and plugins in `gradle/libs.versions.toml`.
- **Implementation:**
  - Created cohesive bundles (`compose`, `koin`, `room`, `arrow`, `testing`).
  - Standardized convention scripts (`common-android-base.gradle`, `common-android-feature.gradle`, `common-android-shared.gradle`).

### 3.7 Google Maps Compose & Location Tracker (Issues #10, #172)
- **Challenge:** Managing hardware GPS state, dynamic permissions, and camera animations in declarative Compose.
- **Solution:** Built `:feature:location-tracker` using `maps-compose` and `FusedLocationProviderClient`.
- **Implementation:**
  - Integrated `DisposableEffect` broadcast listeners to track system location provider toggles without memory leaks.
  - Implemented smart camera follow: auto-centers on user motion, but cleanly suspends auto-follow when the user pans manually.

### 3.8 File Downloader with Android DownloadManager (Issue #56)
- **Challenge:** Offload large file downloads to the Android OS with status updates and notification integration.
- **Solution:** Developed `:feature:file-downloader` wrapping `android.app.DownloadManager`.
- **Implementation:**
  - Clean separation of `DownloaderDataSource` interface and `AndroidDownloaderDataSourceImpl`.
  - ViewModel tracks active download IDs and reports download status states.

### 3.9 Compose Lint Rules & Static Analysis (Issue #74 & ADR 0001)
- **Challenge:** Prevent common Compose performance pitfalls (unstable parameters, unnecessary recompositions) and structured concurrency antipatterns.
- **Solution:** Integrated Slack Compose Lints and custom Lint checks.
- **Implementation:**
  - Enforced `kotlinx.collections.immutable` for all UI state collections to guarantee Compose stability.
  - Added ADR 0001 rules ensuring coroutine scopes are tied strictly to ViewModel lifecycles and structured concurrency trees.

---

## 4. Planned & In-Flight Exercises (The Engineering Roadmap)

### 4.1 Scoped ViewModels Native to Jetpack Compose (Issue #192)
- **Context:** Lifecycle 2.11 introduced Compose-native scoped ViewModels, allowing ViewModels to be bound directly to specific sub-graphs or composable scopes rather than the entire activity or navigation destination.
- **Exercise Goals:**
  - Evaluate lifecycle retention differences between Navigation 3 backstack entries and sub-composable scopes.
  - Benchmark memory reclamation when popping nested sub-destinations.
  - Test migration paths for `:feature:movies` adaptive dual-pane detail ViewModel scoping.

### 4.2 Konsist Architectural Linter (Issue #116)
- **Context:** Ensuring architectural boundaries (e.g., ViewModels must remain `internal`, features must not depend on peer features) is currently manual or dependent on code reviews.
- **Exercise Goals:**
  - Introduce `com.lemonappdev:konsist` unit tests under `:shared:testing`.
  - Automate rules:
    - Assert that all classes extending `BaseViewModel` reside in a `ui.viewmodel` package.
    - Verify that no `:feature:*` module references another `:feature:*` module.
    - Ensure all public repository interfaces reside in the domain layer.

### 4.3 Biometrics Integration with Jetpack Compose (Issue #67)
- **Context:** Integrating hardware biometric authentication (`BiometricPrompt`) cleanly with MVI states.
- **Exercise Goals:**
  - Add biometric login to `:feature:authentication`.
  - Provide fallback to PIN/password on unsupported devices.
  - Abstract biometric callbacks into a reactive Kotlin `Flow<BiometricResult>`.

### 4.4 Kotlin Multiplatform (KMP) Shared Core Evaluation
- **Context:** Feasibility study on sharing domain logic, use cases, and networking across Android and iOS.
- **Exercise Goals:**
  - Evaluate migrating `:shared:core`, `:shared:api`, and domain use cases to `commonMain`.
  - Benchmark Ktor Client as an alternative to Retrofit.
  - Assess Compose Multiplatform (CMP) for shared UI rendering.

---

## 5. Playgrounds & Modules Feature Matrix

The following table provides a comprehensive overview of every feature module in the repository:

| Module | Feature / Playground | Architecture | Networking / Persistence | Form Factor & UI | Test Coverage | Status |
|---|---|---|---|---|---|---|
| `:feature:posts` | Posts feed & details | MVI + Arrow Optics | Retrofit + Room SSOT + WorkManager | Adaptive List-Detail (Phone + Tablet) | JUnit 5, Kotest BDD, Roborazzi | ⭐ Complete (Reference Module) |
| `:feature:movies` | TMDB movie search & browse | MVI + Arrow Optics | Retrofit + TMDB API + Paging 3 | Navigation 3 Adaptive Panes | JUnit 5 unit tests | ⭐ Complete |
| `:feature:location-tracker` | Real-time map & GPS tracker | MVI + Arrow Optics | FusedLocationProviderClient + Maps SDK | Fullscreen Google Map with HUD overlay | Manual verification | Complete |
| `:feature:authentication` | Login & signup forms | MVI + BaseViewModel | Local memory validation | Clean form layout with field validation | JUnit 5 unit tests | Complete |
| `:feature:settings` | User preferences & app config | MVI + Arrow Optics | In-memory / StateFlow | Categorized settings list | JUnit 5 unit tests | Complete |
| `:feature:notifications` | Push notification permission & test | MVI + BaseViewModel | NotificationManager + API 33 permissions | Permission request & trigger card UI | Manual verification | Complete |
| `:feature:file-downloader` | Background file download manager | MVI + BaseViewModel | Android `DownloadManager` | Download status progress card | Manual verification | Complete |
| `:feature:game:ballclicker` | Canvas clicker reaction game | Compose Canvas State | Pure in-memory game loop | Custom drawn animated canvas | Manual verification | Complete |
| `:feature:my-playground` | Rapid prototyping scratchpad | MVI + BaseViewModel | Standalone local UI sandbox | Clean Scaffold with focus-requester text input | PreviewLightDark | 🔨 Active Playground |
| `:feature:home` | Main playground directory | Compose UI | Navigation route catalog | Scrollable card grid of experiments | Unit tests | Complete |
| `:feature:root` | Shell aggregator & host | Jetpack Navigation 3 | None (Navigation routing only) | Single Activity (`RootActivity`) | Manual verification | Complete |

---

## 6. Developer Guide: Creating a New Exercise or Playground

Follow this step-by-step guide to scaffold a new feature playground in the repository:

### Step 1: Create the Feature Module
1. In `settings.gradle`, register the new module:
   ```groovy
   include ':feature:my-new-feature'
   ```
2. Create `feature/my-new-feature/build.gradle`:
   ```groovy
   apply from: "$rootDir/buildSystem/gradle/common-android-feature.gradle"

   android {
       resourcePrefix = 'mynewfeature'
       namespace = 'com.alxnophis.jetpack.mynewfeature'
   }

   dependencies {
       // Add specific shared or library dependencies
   }
   ```
3. Add module constant to `buildSrc/src/main/java/Modules.kt`:
   ```kotlin
   const val FEATURE_MY_NEW_FEATURE = ":feature:my-new-feature"
   ```

### Step 2: Define the MVI Contract
Create `ui/contract/MyNewFeatureContract.kt`:
```kotlin
sealed interface MyNewFeatureEvent {
    data object Initialized : MyNewFeatureEvent
    data object GoBackRequested : MyNewFeatureEvent
}

@optics
data class MyNewFeatureState(
    val isLoading: Boolean,
    val data: String,
) {
    companion object {
        val initialState = MyNewFeatureState(isLoading = false, data = "")
    }
}
```

### Step 3: Implement the ViewModel
Create `ui/viewmodel/MyNewFeatureViewModel.kt`:
```kotlin
internal class MyNewFeatureViewModel(
    initialState: MyNewFeatureState = MyNewFeatureState.initialState,
) : BaseViewModel<MyNewFeatureEvent, MyNewFeatureState>(initialState) {

    override fun handleEvent(event: MyNewFeatureEvent) {
        when (event) {
            is MyNewFeatureEvent.Initialized -> { /* handle */ }
            is MyNewFeatureEvent.GoBackRequested -> { /* handle */ }
        }
    }
}
```

### Step 4: Build the Composable UI
Create `ui/composable/MyNewFeatureScreen.kt` using `AppTheme` and `Scaffold`:
```kotlin
@Composable
internal fun MyNewFeatureScreen(
    state: MyNewFeatureState,
    onEvent: (MyNewFeatureEvent) -> Unit,
) {
    AppTheme {
        Scaffold(
            topBar = {
                CoreTopBar(
                    title = "My New Feature",
                    onBack = { onEvent(MyNewFeatureEvent.GoBackRequested) },
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { paddingValues ->
            // UI Content
        }
    }
}
```

### Step 5: Wire Dependency Injection and Navigation
1. Create `di/MyNewFeatureModule.kt` defining the Koin module:
   ```kotlin
   val myNewFeatureModule = module {
       viewModel { MyNewFeatureViewModel() }
   }
   ```
2. Register the module in `app/.../JetpackApp.kt` and `feature/root/.../FeatureModules.kt`.
3. In `:feature:root`, add the destination route to `Route.kt` and connect the composable in `Navigation.kt`.
4. In `:feature:home`, add an entry to `HomeContent.kt` to allow users to navigate to the new playground.

### Step 6: Add Unit & Screenshot Tests
1. Unit test the ViewModel in `src/test/` extending `BaseViewModelUnitTest`.
2. Verify flow emissions using Turbine.
3. Record Roborazzi screenshot baselines:
   ```bash
   ./gradlew :feature:my-new-feature:recordRoborazziDebug
   ```

---

## 7. Useful Reference Links & Resources
- [Android Architecture Guidelines](https://developer.android.com/topic/architecture)
- [Jetpack Compose Adaptive Layouts](https://developer.android.com/develop/ui/compose/layouts/adaptive)
- [Arrow-kt Documentation (Either & Optics)](https://arrow-kt.io/)
- [Koin Dependency Injection](https://insert-koin.io/)
- [Roborazzi Screenshot Testing](https://github.com/takahirom/roborazzi)
- [Slack Compose Lint Rules](https://slackhq.github.io/compose-lints/)
