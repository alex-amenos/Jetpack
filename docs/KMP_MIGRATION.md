# Kotlin Multiplatform (KMP) Migration Evaluation & Roadmap

## 1. Executive Summary

This document evaluates the feasibility, architectural impact, technical effort, and phased migration strategy for transforming the **Jetpack** Android application into a **Kotlin Multiplatform (KMP)** project targeting **Android** and **iOS** (with Compose Multiplatform for shared UI).

### Key Takeaways
- **High Architectural Readiness:** The codebase is exceptionally well-suited for KMP. The separation of concerns (Clean Architecture, MVI, unidirectional data flow, Arrow functional patterns, and modularization) ensures that domain, use case, state, and repository interfaces can migrate to `commonMain` with minimal friction.
- **Modern Tech Stack Alignment:** The project already uses Kotlin 2.4.20, Koin 4.2.2, Arrow 2.2.3, kotlinx-coroutines 1.11, kotlinx-serialization 1.11, AndroidX Room 2.8.5, and AndroidX DataStore 1.1.7—all of which have official or first-class KMP support.
- **Main Technical Pivots Required:**
  1. **Networking:** Migrate from Retrofit + OkHttp to **Ktor Client** (or Ktorfit) in `:shared:api`.
  2. **Image Loading:** Upgrade from Coil 2.7 to **Coil 3.x** (`io.coil-kt.coil3`), which natively supports KMP.
  3. **UI Layer:** Migrate from AndroidX Jetpack Compose to **Compose Multiplatform (CMP)**.
  4. **Platform-Specific Hardware/OS APIs:** Abstract Android-only components (`FusedLocationProviderClient`, `DownloadManager`, `NotificationManager`, `AndroidKeyStore`) behind `expect`/`actual` interfaces.
  5. **Testing:** Standardize `commonTest` on `kotlin.test`, Kotest assertions, and test fakes (replacing Mockito for multiplatform code).

---

## 2. Dependency & Tooling Compatibility Matrix

The following table evaluates every major dependency currently declared in `gradle/libs.versions.toml` against KMP readiness:

| Category | Library / Dependency | Current Version | KMP Compatibility | Target Replacement / Strategy |
|---|---|---|---|---|
| **Core & Concurrency** | `kotlin` | `2.4.20` | ✅ 100% Native | Fully supported with K2 compiler across all targets. |
| | `kotlinx-coroutines-core` | `1.11.0` | ✅ 100% Native | Native multiplatform support in `commonMain`. |
| | `kotlinx-coroutines-android` | `1.11.0` | ⚠️ Platform | Retain in `androidMain` only; common code uses `Dispatchers.Default` / `Dispatchers.Main`. |
| | `kotlinx-serialization` | `1.11.0` | ✅ 100% Native | Multiplatform JSON serialization in `commonMain`. |
| | `kotlinx-collections-immutable` | `0.4.0` | ✅ 100% Native | Fully multiplatform; ready for immutable UI state models. |
| **Functional Programming** | `arrow-core`, `arrow-fx-coroutines` | `2.2.3` | ✅ 100% Native | Fully multiplatform (`Either`, `Raise`, functional DSLs). |
| | `arrow-optics` + KSP plugin | `2.2.3` | ✅ Multiplatform | Works across KMP source sets with KSP2 multiplatform configuration. |
| | `arrow-retrofit` | `2.2.3` | ❌ JVM / Retrofit only | Deprecate when migrating to Ktor. Handle errors in Ktor client extensions. |
| **Dependency Injection** | `koin-core` | `4.2.2` | ✅ 100% Native | Native KMP support in `commonMain`. |
| | `koin-android` | `4.2.2` | ⚠️ Android only | Use `koin-core` in `commonMain`; keep `koin-android` in `androidMain` / `:app`. |
| | `koin-android-compose` / `viewmodel` | `4.2.2` | ⚠️ Android only | Replace with `koin-compose` and `koin-compose-viewmodel` (KMP-ready). |
| **Networking & HTTP** | `retrofit` + `converter-kotlinx-serialization` | `3.0.0` | ❌ JVM/Android only | Replace with **Ktor Client** (engines: OkHttp on Android, Darwin on iOS). |
| | `okhttp` + `logging-interceptor` | `5.5.0` | ⚠️ JVM/Android (OkHttp engine) | Use as the underlying engine for Ktor in `androidMain`. |
| | `chucker` | `4.3.1` | ❌ Android only | Retain as debug interceptor in `androidMain` engine. |
| | `okhttp-profiler` | `1.0.8` | ❌ Android only | Retain in `androidMain` debug builds only. |
| **Local Persistence** | `androidx.room` (runtime, ktx, compiler) | `2.8.5` | ✅ Official KMP (2.7+) | AndroidX Room supports Android & iOS with SQLite driver (`BundledSQLiteDriver`). |
| | `androidx.datastore` (preferences) | `1.1.7` | ✅ Official KMP (1.1+) | DataStore Preferences supports KMP via platform file path provider. |
| **UI & Presentation** | `androidx.compose.bom` | `2026.05.01` | ⚠️ AndroidX Compose | Migrate shared UI to **JetBrains Compose Multiplatform (CMP)**. |
| | `androidx.lifecycle-viewmodel` | `2.10.0` | ✅ Official KMP (2.8+) | AndroidX Lifecycle ViewModel & `SavedStateHandle` support KMP. |
| | `androidx.navigation3` | `1.1.2` | ⚠️ AndroidX experimental | Evaluate Jetpack Navigation KMP (2.8+) or Decompose / Voyager. |
| | `coil` | `2.7.0` | ⚠️ Coil 2 is Android-only | Upgrade to **Coil 3.x** (`io.coil-kt.coil3`) with Ktor network fetcher. |
| **System & Hardware APIs** | `google-play-services-location` | `21.3.0` | ❌ Android / GMS only | Abstract with `expect`/`actual`: `FusedLocationProviderClient` (Android) vs `CLLocationManager` (iOS). |
| | `google-maps-compose` | `8.6.0` | ❌ Android only | Platform view interop (`UIKitView` for Apple Maps/Google Maps on iOS) or MapLibre KMP. |
| | `androidx.work` (WorkManager) | `2.10.5` | ❌ Android only | Abstract background work with platform-specific schedulers (iOS `BGTaskScheduler`). |
| | Android `DownloadManager` | Android OS | ❌ Android OS only | Abstract with `expect`/`actual`: `DownloadManager` (Android) vs `NSURLSessionDownloadTask` (iOS). |
| | Android `NotificationManager` | Android OS | ❌ Android OS only | Abstract with `expect`/`actual`: `NotificationManager` (Android) vs `UNUserNotificationCenter` (iOS). |
| | Android Keystore (`Crypto.kt`) | Android OS | ❌ Android OS only | Abstract `Crypto`: AndroidKeyStore on Android, Apple Keychain / CryptoKit on iOS. |
| **Logging** | `timber` | `5.0.1` | ❌ Android only | Replace with **Kermit** (Touchlab) or **Napier** for multiplatform logging. |
| **Testing** | `junit-jupiter` (JUnit 5) | `5.13.2` | ❌ JVM only | Use `kotlin.test` for `commonTest`; retain JUnit 5 for Android/JVM-specific tests. |
| | `mockito` / `mockito-kotlin` | `5.24.0` | ❌ JVM bytecode only | Use test fakes or **Mokkery** (KSP-based KMP mocking) in `commonTest`. |
| | `turbine` | `1.2.1` | ✅ 100% Native | Multiplatform Flow testing works seamlessly. |
| | `kotest-assertions` | `6.2.5` | ✅ Multiplatform | Kotest assertions core supports multiplatform. |
| | `roborazzi` / `robolectric` | `1.74` / `4.17` | ⚠️ JVM / Android only | Retain for Android screenshot tests in `androidUnitTest`. |

---

## 3. Module Feasibility & Effort Matrix

The repository features 1 Application shell, 4 Shared modules, and 11 Feature modules. Below is the phased feasibility classification:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        MODULE READINESS TIERS                          │
├────────────────────────────────┬───────────────────────────────────────┤
│ Tier 1: Low Effort / Immediate │ :shared:kotlin                        │
│ (Pure Kotlin / Minimal Android)│ :feature:authentication               │
│                                │ :feature:game:ballclicker             │
│                                │ :feature:my-playground                │
│                                │ :feature:home                         │
│                                │ :feature:settings                     │
├────────────────────────────────┼───────────────────────────────────────┤
│ Tier 2: Medium Effort          │ :shared:api                           │
│ (Ecosystem Migration - Ktor,   │ :shared:core                          │
│  Room KMP, CMP UI)             │ :shared:testing                       │
│                                │ :feature:posts                        │
│                                │ :feature:movies                       │
├────────────────────────────────┼───────────────────────────────────────┤
│ Tier 3: High Effort            │ :feature:location-tracker             │
│ (Platform OS & Hardware APIs)  │ :feature:file-downloader              │
│                                │ :feature:notifications                │
│                                │ :feature:root                         │
│                                │ :app (and new :iosApp)                │
└────────────────────────────────┴───────────────────────────────────────┘
```

### Module-by-Module Breakdown

#### 1. `:shared:kotlin`
- **Current State:** Contains `Constants.kt` (string literals, numeric constants). Applied `common-android-base.gradle` unnecessarily.
- **Migration Effort:** **Trivial (1 hour).** Convert immediately from `com.android.library` to `kotlin("multiplatform")`. Zero Android dependencies.

#### 2. `:feature:authentication`
- **Current State:** Clean MVI structure. `AuthenticateUseCase` uses pure Kotlin coroutines and Arrow `Either`/`Raise`. Composable login screen uses standard Material 3 forms.
- **Migration Effort:** **Low (1–2 days).** Move domain models, use cases, ViewModels, and Compose UI to `commonMain`. Replace Android resource references with Compose Multiplatform resources.

#### 3. `:feature:game:ballclicker`
- **Current State:** Custom Canvas rendering, pointer input, and coroutine timer logic.
- **Migration Effort:** **Low (1–2 days).** Compose Canvas and PointerInput APIs are 100% portable in Compose Multiplatform.

#### 4. `:feature:home` & `:feature:my-playground`
- **Current State:** Menu list, navigation items, simple playground composables.
- **Migration Effort:** **Low (1–2 days).** Pure domain models and standard Compose layouts.

#### 5. `:feature:settings`
- **Current State:** Uses AndroidX DataStore for user preferences (`SettingsPreferences`).
- **Migration Effort:** **Low to Medium (2–3 days).** AndroidX DataStore 1.1+ is multiplatform. The only required change is abstracting the file directory path via an `expect`/`actual` function:
  ```kotlin
  // commonMain
  expect fun producePath(fileName: String): String
  ```

#### 6. `:shared:api`
- **Current State:** Dual Retrofit factories (`TheMovieDbRetrofitFactory`, `JsonPlaceholderRetrofitFactory`), OkHttp interceptors (`NetworkStatusInterceptor`, `ChuckerInterceptor`, `OkHttpProfilerInterceptor`), Arrow Retrofit adapter.
- **Migration Effort:** **Medium (3–5 days).**
  - Implement a common `HttpClient` using Ktor with `ContentNegotiation`, `kotlinx.serialization`, and default request headers.
  - Implement engines: `OkHttp` on Android (supporting Chucker and profiler), `Darwin` on iOS.
  - Convert API services from Retrofit interfaces (`@GET`, `@Query`) to Ktor client service classes.
  - Wrap Ktor requests in Arrow `Either.catch` to preserve domain error signatures.

#### 7. `:shared:core`
- **Current State:** `BaseViewModel`, custom UI components, theme definitions, `Crypto.kt`, `Timber` logging, Android string/drawable resources.
- **Migration Effort:** **Medium (4–6 days).**
  - **BaseViewModel:** Modernize with `androidx.lifecycle.ViewModel` in `commonMain`.
  - **Logging:** Replace `Timber` calls with `Kermit`.
  - **Crypto:** Extract `interface CryptoService` with `AndroidCryptoService` (AndroidKeyStore) and `IosCryptoService` (Apple Keychain / CryptoKit).
  - **Theme & Composables:** Convert Android View-based status bar controllers (`WindowCompat`) to platform side-effects.

#### 8. `:feature:posts`
- **Current State:** Offline-first caching with Room (`PostDao`, `PostsDatabase`), remote datasource via `JsonPlaceholderServices`, list-detail adaptive navigation.
- **Migration Effort:** **Medium (3–4 days).**
  - Room 2.8+ is multiplatform. Replace `Room.databaseBuilder(context, ...)` with common `Room.databaseBuilder<PostsDatabase>()` and inject platform `BundledSQLiteDriver`.
  - Hook into Ktor remote data source.

#### 9. `:feature:movies`
- **Current State:** TMDB integration, AndroidX Paging 3 (`MoviePagingSource`), Coil image loading, list-detail adaptive layout.
- **Migration Effort:** **Medium (3–4 days).**
  - AndroidX Paging 3.3+ supports KMP (`androidx.paging:paging-common`, `androidx.paging:paging-compose`).
  - Upgrade Coil to 3.x with Ktor network fetcher.

#### 10. `:shared:testing`
- **Current State:** JUnit 5, Mockito-Kotlin, Turbine, Robolectric, Roborazzi rules.
- **Migration Effort:** **Medium (3–4 days).**
  - Split into `commonTest` utilities (`kotlin.test`, Turbine, Fake repositories) and `androidUnitTest` utilities (Robolectric, Roborazzi screenshot harnesses).

#### 11. `:feature:location-tracker`
- **Current State:** Google Play Services Fused Location, Google Maps Compose, WorkManager.
- **Migration Effort:** **High (5–7 days).**
  - Define `LocationDataSource` interface in `commonMain`.
  - Android implementation keeps `FusedLocationProviderClient`.
  - iOS implementation uses Apple `CLLocationManager`.
  - UI: Use an `expect`/`actual` composable for the map view (`GoogleMap` on Android, `UIKitView` embedding `MKMapView` or Google Maps iOS on iOS).

#### 12. `:feature:file-downloader`
- **Current State:** Uses Android `DownloadManager` and `DownloadCompletedReceiver`.
- **Migration Effort:** **High (4–5 days).**
  - `DownloaderDataSource` interface is already cleanly decoupled.
  - Implement `IosDownloaderDataSource` using `NSURLSessionDownloadTask`.

#### 13. `:feature:notifications`
- **Current State:** Android `NotificationManager`, channels, and `NotificationCompat`.
- **Migration Effort:** **High (4–5 days).**
  - Abstract local notification triggers via an `expect`/`actual` `NotificationService` (Android `NotificationCompat` vs iOS `UNUserNotificationCenter`).

#### 14. `:feature:root` & `:app` (plus new `:iosApp`)
- **Current State:** Hosts navigation graph using AndroidX Navigation 3 and Android `RootActivity`.
- **Migration Effort:** **High (4–6 days).**
  - Common root navigation composable shared between platforms.
  - Android retains `RootActivity`.
  - iOS entry point: Create `ComposeUIViewController { RootNavigation() }` consumed by SwiftUI in a standard Xcode project.

---

## 4. Architectural Transformation Strategy

### 4.1. Networking: Retrofit to Ktor Client Migration

Currently, networking relies on Retrofit with OkHttp and an Arrow call adapter:

```kotlin
// CURRENT (shared/api): Android/JVM Retrofit
interface TheMovieDbServices {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int
    ): Either<CallError, MovieSearchResponseApiModel>
}
```

**Target KMP Architecture:**
Ktor provides multiplatform HTTP execution with platform-specific engines:
- **`androidMain`**: `io.ktor:ktor-client-okhttp` (allows plugging in existing Chucker & OkHttp profilers).
- **`iosMain`**: `io.ktor:ktor-client-darwin` (uses iOS native `NSURLSession`).

```kotlin
// TARGET (shared/api commonMain): Ktor + Arrow Either
class TheMovieDbKtorService(private val client: HttpClient) {
    suspend fun getPopularMovies(page: Int): Either<NetworkError, MovieSearchResponseApiModel> =
        Either.catch {
            client.get("movie/popular") {
                parameter("page", page)
            }.body<MovieSearchResponseApiModel>()
        }.mapLeft { it.toNetworkError() }
}
```

### 4.2. Local Persistence: Room KMP & DataStore

AndroidX Room 2.8+ officially supports Kotlin Multiplatform for Android and iOS:
1. Declare database interface in `commonMain`:
   ```kotlin
   @Database(entities = [PostEntity::class, PostsMetadataEntity::class], version = 1)
   @ConstructedBy(PostsDatabaseConstructor::class)
   abstract class PostsDatabase : RoomDatabase() {
       abstract fun postDao(): PostDao
       abstract fun postsMetadataDao(): PostsMetadataDao
   }
   ```
2. Instantiate via platform builders:
   - In `androidMain`: `Room.databaseBuilder(context, PostsDatabase::class.java, name)`
   - In `iosMain`: `Room.databaseBuilder<PostsDatabase>(name, factory = { PostsDatabase::class.instantiateImpl() })`
3. Configure `BundledSQLiteDriver` in Koin DI.

### 4.3. UI Layer: Compose Multiplatform (CMP)

- Shared UI moves to `commonMain` in each feature module.
- Gradle plugin: `org.jetbrains.compose` replaces AndroidX Compose compiler directly.
- Multiplatform resources: Use JetBrains Compose Resources (`Res.string.*`, `Res.drawable.*`) instead of `R.string.*` and `R.drawable.*`.
- ViewModels: Keep `BaseViewModel<Event, State>` extending `androidx.lifecycle.ViewModel` (which now supports `commonMain`).

### 4.4. Platform Services & Expect/Actual Pattern

For hardware and platform-tied APIs, use interface-based inversion of control via Koin rather than scattered `expect`/`actual` functions:

```
┌──────────────────────────────────────────────┐
│           commonMain (Feature)               │
│                                              │
│   interface DownloaderDataSource {           │
│       fun downloadFile(url: String): Long    │
│   }                                          │
└───────────────────────▲──────────────────────┘
                        │
       ┌────────────────┴────────────────┐
       │                                 │
┌──────┴───────────────┐     ┌───────────┴───────────┐
│     androidMain      │     │        iosMain        │
│                      │     │                       │
│ AndroidDownloader-   │     │ IosDownloader-        │
│ DataSourceImpl       │     │ DataSourceImpl        │
│ (DownloadManager)    │     │ (NSURLSession)        │
└──────────────────────┘     └───────────────────────┘
```

---

## 5. Build System & Gradle Infrastructure

### 5.1. Target Module Structure

Replace monolithic Android library scripts with modern Gradle Kotlin DSL Convention Plugins:

```
Jetpack/
├── build-logic/                          # Gradle Convention Plugins
│   ├── src/main/kotlin/
│   │   ├── jetpack.kmp.library.gradle.kts
│   │   ├── jetpack.kmp.feature.gradle.kts
│   │   ├── jetpack.kmp.compose.gradle.kts
│   │   └── jetpack.android.app.gradle.kts
├── shared/
│   ├── kotlin/                           # Pure KMP
│   ├── core/                             # KMP + Compose
│   ├── api/                              # KMP Ktor client
│   └── testing/                          # Common test utilities + Android JVM harnesses
├── feature/
│   ├── authentication/                   # KMP Feature (commonMain + androidMain + iosMain)
│   ├── posts/
│   └── ...
├── app/                                  # Android Application Shell
└── iosApp/                               # Native iOS App (Xcode / SwiftUI entry)
```

### 5.2. KMP Source Sets Topology

```kotlin
// Example build.gradle.kts for a feature module
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.ksp)
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FeaturePosts"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:core"))
            implementation(project(":shared:api"))
            implementation(libs.arrow.core)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.compose.material3)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        iosMain.dependencies {
            // iOS-specific frameworks (Foundation, UIKit, etc.)
        }
    }
}
```

---

## 6. Testing Strategy Across Multiplatform

| Test Scope | Target Source Set | Recommended Frameworks | Notes |
|---|---|---|---|
| **Domain UseCases & Reducers** | `commonTest` | `kotlin.test`, `Turbine`, `Kotest assertions` | Pure business logic tests execute uniformly on JVM and iOS Simulator. |
| **Repository & Data Flow** | `commonTest` | `kotlin.test`, Test Fakes, Ktor Mock Engine | Replace Mockito with hand-crafted fakes or Mokkery. |
| **Compose UI State & Components** | `commonTest` / `androidUnitTest` | Compose UI Test library | Run screenshot tests via Roborazzi in `androidUnitTest` / JVM. |
| **Platform Implementations** | `androidUnitTest` / `iosTest` | Robolectric (Android), XCTest / Kotlin Native test (iOS) | Test OS-specific bindings (Keystore, CoreLocation). |
| **End-to-End User Journeys** | `:app` / `.maestro` | Maestro UI Automator | Maestro works across both Android and iOS without modifying test flows. |

---

## 7. Phased Implementation Roadmap

```
PHASE 0: Foundations & Tooling (1-2 weeks)
├── Setup build-logic convention plugins for KMP
├── Migrate Logging: Timber -> Kermit
├── Upgrade Coil 2 -> Coil 3
└── Convert :shared:kotlin to pure KMP

PHASE 1: Core & Networking (2-3 weeks)
├── Migrate :shared:core BaseViewModel to KMP Lifecycle ViewModel
├── Refactor :shared:api from Retrofit to Ktor Client
└── Verify Android app regression-free with Ktor & Kermit

PHASE 2: Data & Persistence Migration (2-3 weeks)
├── Migrate :feature:settings DataStore to KMP
├── Migrate :feature:posts Room Database to Room KMP
└── Update Koin modules to koin-core and koin-compose

PHASE 3: UI to Compose Multiplatform (3-4 weeks)
├── Migrate shared UI components in :shared:core to CMP
├── Migrate Tier 1 Features (:authentication, :game, :home, :my-playground)
└── Migrate Tier 2 Features (:posts, :movies)

PHASE 4: Hardware & OS Services Bridging (3-4 weeks)
├── Abstract and implement iOS :feature:file-downloader (NSURLSession)
├── Abstract and implement iOS :feature:notifications (UNUserNotificationCenter)
└── Abstract and implement iOS :feature:location-tracker (CoreLocation / MapLibre)

PHASE 5: iOS Application Shell & CI/CD (2-3 weeks)
├── Create :iosApp Xcode project with SwiftUI entry point
├── Implement root navigation bridge (ComposeUIViewController)
├── Add iOS build & test matrix to GitHub Actions workflow
└── Verify end-to-end user journeys via Maestro on iOS Simulator
```

---

## 8. Risks, Trade-offs & Recommendations

### Strategic Recommendations
1. **Incremental Adoption (Do Not Rewrite at Once):** Because KMP allows compiling `androidMain` into standard Android AARs, every module can be converted to KMP one by one while keeping the Android app fully functional and continuously shippable.
2. **Favor Test Fakes over Mocking Frameworks:** Avoid heavy native mocking libraries in `commonTest`. Hand-crafted fakes (e.g., `FakeMovieRepository.kt`, which the project already utilizes) provide superior performance, type safety, and zero native runtime issues.
3. **Keep Android Lint & Static Analysis:** Continue leveraging Android Lint and Ktlint on `androidMain` and `commonMain` to maintain code standards during migration.
4. **Coil 3 & Ktor First:** Doing the Ktor and Coil 3 migrations while still on Android is the lowest-risk path: you validate the networking and image loading in production before ever running on iOS.
