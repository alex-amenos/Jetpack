# Navigation Testing Plan

Plan to test the application navigation defined in `:feature:root` at `feature/root/src/main/java/com/alxnophis/jetpack/root/ui/navigation/Navigation.kt`.

---

## 1. Context & Architecture

The application uses **Jetpack Navigation 3** (`androidx.navigation3`) combined with **Material 3 Adaptive** layouts (`androidx.compose.material3.adaptive.navigation3`).

- **Entry Point:** `Route.Home`
- **Feature Destinations (9 Features):**
  - `Authentication`
  - `FileDownloader`
  - `GameBallClicker`
  - `LocationTracker`
  - `Movies`
  - `MyPlayground`
  - `Notifications`
  - `Posts`
  - `Settings`
- **Sub-flows & Special Cases:**
  - **Authentication Flow:** Successful login transitions to `Route.Authorized(email)`. Back navigation from `Authorized` pops both `Route.Authentication` and `Route.Authorized`, preventing the user from returning to the login form.
  - **Adaptive List-Detail (Movies & Posts):** Selecting a movie or post replaces the existing detail route if one is already active (avoiding duplicate detail entries on the stack). List panes configure empty/placeholder fallback composables (`MovieNotSelectedComposable`, `PostNotSelectedComposable`) via `ListDetailSceneStrategy.listPane`.
  - **Standard Back Navigation:** Standard destinations pop the top route from the back stack (`backStack.removeLastOrNull()`).

---

## 2. Test Strategy & Pyramid

```
               ▲
              / \     Layer 3: End-to-End Journey Tests (:app/src/androidTest)
             /   \    - Full user journeys through RootActivity & Robots
            /-----\
           /       \    Layer 2: NavEntry Provider & Metadata Tests (:feature:root/src/test)
          /         \   - Verifies all routes resolve and have correct scene metadata
         /-----------\
        /             \  Layer 1: Navigation Logic Unit Tests (:feature:root/src/test)
       /               \ - Pure JVM tests for all backstack transitions & edge cases
      -------------------
```

### Layer 1: Navigation Logic Unit Tests (Pure JVM, JUnit 5 + Kluent)
- **Target:** State transitions and backstack operations decoupled from Compose runtime.
- **Approach:** Extract navigation actions into an internal `NavigationNavigator` state holder operating on `MutableList<NavKey>`.
- **Benefits:** Fast execution (milliseconds), runs in CI without emulator/Robolectric, 100% deterministic branch coverage.

### Layer 2: NavEntry Provider & Metadata Verification (JVM, JUnit 5 + Nav3)
- **Target:** `entryProvider` configuration inside `Navigation.kt`.
- **Approach:** Call the entry provider with each `Route` type and assert:
  - Every defined `Route` resolves to a non-null `NavEntry`.
  - `Route.Movies` and `Route.Posts` include `ListDetailSceneStrategy.listPane` metadata.
  - `Route.MovieDetail` and `Route.PostDetail` include `ListDetailSceneStrategy.detailPane` metadata.

### Layer 3: End-to-End Instrumented Tests (`:app/src/androidTest`)
- **Target:** Full integration on device / emulator using Compose Test Rule and the Screen Robot pattern.
- **Current status:** Existing tests cover `MoviesJourneyRobotTest` and `PostsJourneyRobotTest`.
- **Additions:** Add robot tests for authentication flow (`AuthenticationJourneyRobotTest`) and verify tile-to-feature navigation from Home.

---

## 3. Test Cases Specification

### A. Navigation Logic (`NavigationNavigatorTest`)

| # | BDD Scenario | Initial Stack | Action | Expected Stack |
|---|-------------|---------------|--------|----------------|
| 1 | `GIVEN initial state WHEN initialized THEN initial route is Home` | `[]` | Init | `[Route.Home]` |
| 2 | `GIVEN Home WHEN navigateToFeature for each feature THEN correct Route pushed` | `[Route.Home]` | `navigateToFeature(feature)` for all 9 `Feature` items | `[Route.Home, Route.<Feature>]` |
| 3 | `GIVEN Authentication WHEN login succeeds THEN Authorized route added with email` | `[Route.Home, Route.Authentication]` | `onAuthenticationSuccess("user@test.com")` | `[Route.Home, Route.Authentication, Route.Authorized("user@test.com")]` |
| 4 | `GIVEN Authorized screen WHEN onBack called THEN auth routes are popped` | `[Route.Home, Route.Authentication, Route.Authorized("email")]` | `onAuthorizedBack()` | `[Route.Home]` |
| 5 | `GIVEN Movies screen WHEN first movie selected THEN MovieDetail added` | `[Route.Home, Route.Movies]` | `onMovieSelected(101)` | `[Route.Home, Route.Movies, Route.MovieDetail(101)]` |
| 6 | `GIVEN MovieDetail open WHEN different movie selected THEN previous detail replaced` | `[Route.Home, Route.Movies, Route.MovieDetail(101)]` | `onMovieSelected(202)` | `[Route.Home, Route.Movies, Route.MovieDetail(202)]` |
| 7 | `GIVEN Posts screen WHEN first post selected THEN PostDetail added` | `[Route.Home, Route.Posts]` | `onPostSelected(10L)` | `[Route.Home, Route.Posts, Route.PostDetail(10L)]` |
| 8 | `GIVEN PostDetail open WHEN different post selected THEN previous detail replaced` | `[Route.Home, Route.Posts, Route.PostDetail(10L)]` | `onPostSelected(20L)` | `[Route.Home, Route.Posts, Route.PostDetail(20L)]` |
| 9 | `GIVEN multiple screens WHEN onBack called THEN topmost screen is popped` | `[Route.Home, Route.Settings]` | `onBack()` | `[Route.Home]` |
| 10 | `GIVEN single screen in stack WHEN onBack called THEN stack becomes empty without crashing` | `[Route.Home]` | `onBack()` | `[]` |

### B. Route & Scene Metadata Resolution (`NavigationEntryProviderTest`)

| # | Test Scenario | Verified Items |
|---|--------------|----------------|
| 1 | `GIVEN all Route types WHEN resolving entry THEN valid NavEntry returned for each` | Parameterized test verifying all 12 `Route` subclasses resolve successfully |
| 2 | `GIVEN Movies and Posts routes WHEN checking entry metadata THEN listPane metadata exists` | `entry.metadata` contains `ListDetailSceneStrategy.listPane` configuration |
| 3 | `GIVEN MovieDetail and PostDetail routes WHEN checking entry metadata THEN detailPane metadata exists` | `entry.metadata` contains `ListDetailSceneStrategy.detailPane` configuration |

### C. End-to-End Robot Tests (`:app/src/androidTest`)

| # | Test Scenario | Robot Flow |
|---|--------------|------------|
| 1 | Authentication flow & backstack cleanup | `HomeScreenRobot.navigateToAuth()` $\to$ `AuthRobot.login("user@test.com")` $\to$ `AuthorizedRobot.assertEmail("user@test.com").clickBack()` $\to$ `HomeScreenRobot.assertHomeDisplayed()` |
| 2 | Complete navigation verification | Traverse all top-level features from Home and ensure back navigation reliably returns to Home. |

---

## 4. Implementation Steps

1. **Refactor `Navigation.kt` for Testability (Non-breaking):**
   - Extract navigation action logic into an internal `NavigationNavigator(val backStack: MutableList<NavKey>)` class.
   - Update `Navigation` composable signature to support dependency injection with sensible defaults:
     ```kotlin
     @Composable
     fun Navigation(
         modifier: Modifier = Modifier,
         backStack: MutableList<NavKey> = rememberNavBackStack(Route.Home),
         navigator: NavigationNavigator = remember(backStack) { NavigationNavigator(backStack) },
     )
     ```
   - Extract the entry provider builder into an internal function:
     ```kotlin
     internal fun createNavigationEntryProvider(
         navigator: NavigationNavigator,
         onBack: () -> Unit,
     ): (NavKey) -> NavEntry<*>
     ```
2. **Implement Unit Tests in `:feature:root/src/test/kotlin/com/alxnophis/jetpack/root/ui/navigation/`:**
   - Create `NavigationNavigatorTest.kt`: JUnit 5 tests covering all 10 transition scenarios using Kluent assertions.
   - Create `NavigationEntryProviderTest.kt`: Unit tests verifying route key resolution and adaptive metadata.
3. **Extend E2E Robot Tests in `:app/src/androidTest/` (Optional / Follow-up):**
   - Implement `AuthenticationJourneyRobotTest.kt` validating full login journey and backstack pop behavior.
4. **Verification Commands:**
   ```bash
   ./gradlew :feature:root:testDebugUnitTest
   ./gradlew :feature:root:ktlintCheck
   ./gradlew :feature:root:lintDebug
   ```
