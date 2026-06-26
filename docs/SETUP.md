# Project Setup

To fully run and test all the features in this project, you need to configure a few API keys. The app can still build without them, but Google Maps and TMDB-related features will be disabled or fail at runtime.

## Prerequisites

- [Android Studio](https://developer.android.com/studio)
- [JDK 21](https://adoptium.net/) (Recommended: Zulu or Eclipse Temurin)
- Git

## API Keys Configuration

Create a file named `local.properties` in the root directory of the project (if it doesn't already exist). This file is git-ignored to prevent your secrets from being pushed to the repository.

Add the following properties to your `local.properties` file:

```properties
# Google Maps SDK for Android
JETPACK_GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here

# TMDB (The Movie Database) API Read Access Token
THEMOVIEDB_ORG_API_TOKEN=your_tmdb_api_read_access_token_here
```

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
- Look for the **API Read Access Token (v4 auth)** or the **API Key (v3 auth)**. The project is expecting the token as `THEMOVIEDB_ORG_API_TOKEN`.
- Copy the token and paste it into `local.properties` as `THEMOVIEDB_ORG_API_TOKEN`.

## Build and Run

Once the `local.properties` file is configured with the correct API keys:
1. Open the project in Android Studio.
2. Sync Project with Gradle Files.
3. Select the `app` run configuration and deploy it to your emulator or physical device.

For more details on the application architecture, check [here](https://deepwiki.com/alex-amenos/Jetpack).
