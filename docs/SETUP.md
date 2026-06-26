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
