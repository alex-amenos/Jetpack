# Project Setup

Follow these steps to configure, build, and run the Jetpack app locally.

## Prerequisites

- Git
- Android Studio
- JDK 21 (Android Studio's bundled JDK is suitable if it is version 21)
- An Android emulator or a physical Android device running API 26 or newer

The project includes the Gradle wrapper, so you do not need to install Gradle separately. Android Studio can install the Android SDK components required by the project when you sync.

## 1. Get the project

Clone the repository and open its root directory in Android Studio:

```shell
git clone https://github.com/alex-amenos/Jetpack.git
cd Jetpack
```

In Android Studio, choose **Open** and select the `Jetpack` directory. If prompted, trust the project and sync it with Gradle. In **Settings > Build, Execution, Deployment > Build Tools > Gradle**, set **Gradle JDK** to JDK 21.

## 2. Configure API credentials

API credentials are optional for building and launching the app. Without them, features that use Google Maps or TMDB will not work correctly.

The project reads credentials as Gradle project properties. The recommended option is to add them to your user-level Gradle properties file, which is outside the repository and should not be committed:

- macOS or Linux: `~/.gradle/gradle.properties`
- Windows: `%USERPROFILE%\.gradle\gradle.properties`

Add the properties for the services you want to use:

```properties
JETPACK_GOOGLE_MAPS_API_KEY=your_google_maps_api_key
THEMOVIEDB_ORG_API_TOKEN=your_tmdb_api_read_access_token
```

Create a Google Maps API key in the [Google Cloud Console](https://console.cloud.google.com/), enable **Maps SDK for Android**, and use the key for `JETPACK_GOOGLE_MAPS_API_KEY`. Create a TMDB account, request API access in your account's **API** settings, and use the **API Read Access Token (v4 auth)** for `THEMOVIEDB_ORG_API_TOKEN`. The project does not use a TMDB v3 API key for this property.

Alternatively, pass the values to a Gradle command with `-P`:

```shell
./gradlew :app:assembleDebug \
  -PJETPACK_GOOGLE_MAPS_API_KEY=your_google_maps_api_key \
  -PTHEMOVIEDB_ORG_API_TOKEN=your_tmdb_api_read_access_token
```

Avoid putting real credentials in committed files, screenshots, or shared command history. Restrict API keys in the provider consoles where possible.

## 3. Build the debug app

From the project root, build the debug APK:

```shell
./gradlew :app:assembleDebug
```

On Windows, use `gradlew.bat :app:assembleDebug`. The Android Gradle Plugin may prompt you to install missing SDK components; accept the licenses and install the requested components.

## 4. Launch the app

In Android Studio, start an emulator or connect an Android device with USB debugging enabled. Select the **app** run configuration and click **Run**.

Alternatively, install and launch the debug app on a running emulator or connected device:

```shell
./gradlew :app:installDebug
```

The debug application ID is `com.alxnophis.jetpack.debug`. Grant location permission when prompted if you want to use location-based features.

## Useful checks

Run the project's CI checks from the repository root:

```shell
./gradlew ktlintCheck
./gradlew lintDebug
./gradlew koverHtmlReportDebug
```
