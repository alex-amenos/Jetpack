# Project Setup

To fully run and test all the features in this project, you need to configure a few API keys. The app can still build without them, but Google Maps and TMDB-related features will be disabled or fail at runtime.

## Prerequisites

- [Android Studio](https://developer.android.com/studio)
- [JDK 21](https://adoptium.net/) (Recommended: Zulu or Eclipse Temurin)
- Git

## API Keys Configuration

The project reads these values as **Gradle project properties** (via `project.findProperty(...)`).

Recommended: add them to your user Gradle properties file at `~/.gradle/gradle.properties` (this is not committed to the repo):

    # ~/.gradle/gradle.properties
    JETPACK_GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here
    THEMOVIEDB_ORG_API_TOKEN=your_tmdb_api_read_access_token_here

Alternatively, you can pass them on the command line when running Gradle:
`./gradlew :app:assembleDebug -PJETPACK_GOOGLE_MAPS_API_KEY=... -PTHEMOVIEDB_ORG_API_TOKEN=...`

### 1. Google Maps API Key (`JETPACK_GOOGLE_MAPS_API_KEY`)
Used in the `location-tracker` feature to display Google Maps.

- Go to the [Google Cloud Console](https://console.cloud.google.com/).
- Create a new project or select an existing one.
- Navigate to **APIs & Services** > **Library**.
- Search for **Maps SDK for Android** and enable it.
- Go to **APIs & Services** > **Credentials**.
- Click **Create Credentials** > **API key**.
- Copy the API key and paste it into `local.properties` as `JETPACK_GOOGLE_MAPS_API_KEY`.

### 2. TMDB API Token (`THEMOVIEDB_ORG_API_TOKEN`)
Used in the `movies` feature to fetch movie data.

- Create an account on [The Movie Database (TMDB)](https://www.themoviedb.org/).
- Go to your account settings by clicking on your profile icon.
- Select the **API** section from the left sidebar.
- Request an API key by filling out the developer form.
- Look for the **API Read Access Token (v4 auth)**. This project uses it as a Bearer token (`Authorization: Bearer ...`) and does not support the v3 **API Key (v3 auth)** here.
- Copy the token and paste it into `local.properties` as `THEMOVIEDB_ORG_API_TOKEN`.

## Build and Run

Once the `local.properties` file is configured with the correct API keys:
1. Open the project in Android Studio.
2. Sync Project with Gradle Files.
3. Select the `app` run configuration and deploy it to your emulator or physical device.

For more details on the application architecture, check [here](https://deepwiki.com/alex-amenos/Jetpack).

## Library Update Compatibility

The version catalog keeps these dependency updates together:

| Dependency | Previous version | Updated version |
| --- | --- | --- |
| AndroidX Test Rules | 1.6.1 | 1.7.0 |
| AndroidX Test Runner | 1.6.2 | 1.7.0 |
| Roborazzi plugin and libraries | 1.74.0 | 1.76.0 |
| ConstraintLayout Compose | 1.1.1 | 1.1.2 |
| Google Play Services Maps | 19.0.0 | 20.0.0 |
| kotlinx.collections.immutable | 0.4.0 | 0.5.2 |

- **AndroidX Test:** Runner is aligned with Rules at 1.7.0; JUnit extensions remain at 1.3.0.
  `ServiceTestRule.startService` now throws if its intent does not launch a service.
  The project does not currently use this rule. See the
  [upstream Rules changelog](https://github.com/android/android-test/blob/axt_2025_07_30_stable/runner/rules/CHANGELOG.md).
- **Google Maps:** Maps Compose 8.6.0 already uses Maps KTX 6.4.1, which declares Maps SDK 20.0.0.
  This aligns the direct SDK declaration with that dependency chain.
  Maps KTX 6.x raised its minimum Android API to 23; the app already requires API 26.
  No Maps Compose or application minimum-SDK update is needed. See the
  [Maps KTX 6.0.0 release notes](https://github.com/googlemaps/android-maps-ktx/releases/tag/v6.0.0) and
  [6.4.1 version catalog](https://github.com/googlemaps/android-maps-ktx/blob/v6.4.1/gradle/libs.versions.toml).
  Smoke-test the live map, location marker, camera following, gestures, and permission changes on a
  device with Google Play services and a configured Maps API key.
- **Immutable collections:** 0.5.x deprecates copy-returning methods such as `add`, `put`, and `set`
  in favor of `adding`, `putting`, and `replacingAt`. Old names become compilation errors in 0.6.0
  and are removed in 0.7.0. Current project usages are factories, `ImmutableList`, and
  `toImmutableList`, so no call-site migration is needed. Kotlin 2.4.20 already exceeds the
  library's Kotlin 2.3.0 baseline. See the
  [migration guide](https://github.com/Kotlin/kotlinx.collections.immutable/blob/v0.5.2/docs/0.5.0-MIGRATION.md).
- **Roborazzi:** Keep the plugin and capture libraries on the shared catalog version.
  [1.75.0](https://github.com/takahirom/roborazzi/releases/tag/1.75.0) and
  [1.76.0](https://github.com/takahirom/roborazzi/releases/tag/1.76.0) change when generated-preview
  and Compose setup options are applied relative to Activity launch. This can change screenshots;
  precompiled custom `ComposePreviewTester` implementations also require recompilation.
  This project uses explicit Compose tests with `SemanticsNodeInteraction.captureRoboImage`,
  not generated preview tests or custom testers. Run `./gradlew verifyRoborazziDebug` before
  accepting the upgrade. Only use `./gradlew recordRoborazziDebug` after reviewing any visual
  differences; do not overwrite baselines just to make verification pass.
- **ConstraintLayout Compose:** No direct ConstraintLayout API usage was found in the app.
  It remains included in the shared Compose bundle; verify compilation, lint, and screenshots
  with the patch update.
