# Jetpack playgrounds

It is a personal project to experiment with Android development.

ℹ️ Some experiments are not completed in terms of development, you can see on code maybe is missing testing or some data/domain layer.

## Home

It is the home screen where you can select some of experiments of this project.

<img alt="Home screen" src="./images/home_screen.png" width="300" />

## Posts

Load posts from JsonPlaceholder Rest API.<br>
Uses adaptive layouts to support both phone and tablet form factors.<br><br>

This module is the most complete in the project and demonstrates modern Android architecture and robust testing practices:<br>

- **Architecture**: MVI (Model-View-Intent) pattern.
- **UI**: Built entirely with Jetpack Compose.
- **Data Layer**: Offline-first architecture with Room database persistence. Cache-first strategy with network fallback.
- **Background work**: Automatic background refresh every 24 hours.
- **Error Handling**: Functional error handling using Arrow `Either` and typed errors.
- **Testing**: Highly covered with JUnit 5 unit tests, Kotest `BehaviorSpec` BDD tests, and Compose snapshot testing using Roborazzi.

<table>
  <tr>
    <td><img alt="Posts list 1" src="./images/posts_screen_list_1.png" width="300" /></td>
    <td><img alt="Posts list 2" src="./images/posts_screen_list_2.png" width="300" /></td>
  </tr>
  <tr>
    <td><img alt="Posts list in landscape 1" src="./images/posts_screen_list_landscape_1.png" width="500" /></td>
    <td><img alt="Posts list in landscape 2" src="./images/posts_screen_list_landscape_2.png" width="500" /></td>
  </tr>
  <tr>
    <td><img alt="Posts list and detail in landscape 1" src="./images/posts_screen_detail_landscape_1.png" width="500" /></td>
    <td><img alt="Posts list and detail in landscape 2" src="./images/posts_screen_detail_landscape_2.png" width="500" /></td>
  </tr>
  <tr>
    <td><img alt="Posts detail 1" src="./images/posts_screen_detail_1.png" width="300" /></td>
    <td><img alt="Posts detail 2" src="./images/posts_screen_detail_2.png" width="300" /></td>
  </tr>
</table>

## Authentication

Authentication form with SignUp and SignIn.<br><br>
More experiment info:<br>

- Experiment with testing: unit test, snapshot tests and android tests

<img alt="Authentication screen" src="./images/authentication_screen.png" width="300" />

## Settings

Settings options screen.<br><br>
More experiment info:<br>

- Not persisted settings<br>
- Experiment with testing: unit test, snapshot tests and android tests

<img alt="Settings screen" src="./images/settings_screen.png" width="300" />

## Notifications

Push Notifications screen.<br><br>
More experiment info:<br>

- Experiment without testing.

<img alt="Notifications screen" src="./images/notifications_screen.png" width="300" />

## Location tracker

User location tracking with an integrated Google Maps UI, custom UI overlays, and automatic camera framing.

More experiment info:

- **Architecture**: MVI (Model-View-Intent) pattern with `LocationRepository` abstraction.
- **UI**: Jetpack Compose map rendering via `maps-compose`. Edge-to-edge support with safe padding.
- **Location**: Tracks user fine/coarse location dynamically utilizing `FusedLocationProviderClient`.
- **Map Interaction**: Auto-follows the user, smoothly animating the camera. Panning the map manually suspends the auto-follow mode until the user clicks the track button.
- **State Handling**: Uses Arrow Optics for MVI immutable state manipulation. Uses `BroadcastReceiver` inside a `DisposableEffect` to seamlessly handle external system location toggle changes without memory leaks.
- **Testing**: Experiment without testing.

<img alt="Location permissions" src="./images/location_tracker_screen_1.png" width="300" /> <img alt="Location permissions" src="./images/location_tracker_screen_2.png" width="300" />

## Ball clicker (Game)

Ball clicker game (custom view with compose).<br><br>
More experiment info:<br>

- Experiment without testing.

<img alt="Ball clicker game" src="./images/game_ball_clicker_screen.png" width="300" />

## File downloader

Download files using a [DownloadManager](https://developer.android.com/reference/android/app/DownloadManager)<br><br>
More experiment info:<br>

- Experiment without testing.

<img alt="File downloader" src="./images/file_downloader_screen.png" width="300" />
