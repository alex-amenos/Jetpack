# End-to-End Testing Guide

This document describes how to run and write end-to-end (E2E) tests for the Jetpack application.

## Overview

The E2E tests validate complete user journeys through the application, ensuring that all features work correctly together. These tests run on actual Android devices or emulators.

### Test Architecture

**E2E tests are located in the `:app` module** because they test complete user journeys that span multiple features and require `RootActivity`:

- **Application-Level E2E Tests** (`:app` module)
    - Complete user journeys across multiple features
    - Example: Home → Posts → Detail navigation
    - Located in: `app/src/androidTest/`
    - Uses `RootActivity` as entry point

This approach ensures:

- ✅ No circular or incorrect module dependencies
- ✅ Tests can access the full application context
- ✅ Proper separation: E2E tests in `:app`, unit tests in feature modules
- ✅ Follows Android module architecture best practices

## Test Structure

```
app/src/androidTest/java/com/alxnophis/jetpack/
├── e2e/
│   ├── PostsJourneyTest.kt              # Basic E2E tests for Posts feature
│   └── PostsJourneyRobotTest.kt         # ⭐ E2E with Robot pattern (recommended)
└── utils/
    ├── ComposeTestRuleExtensions.kt     # Utility extensions
    └── ScreenRobots.kt                  # Screen Robot pattern implementations
```

## Running E2E Tests

### Prerequisites

1. **Device/Emulator**: You need a running Android device or emulator
   ```bash
   # List available devices
   adb devices
   
   # Or start an emulator using android CLI
   android emulator start
   ```

2. **Dependencies**: All required dependencies are configured in `buildSystem/gradle/common-app-base.gradle`

### Run All E2E Tests

```bash
# Run all E2E tests from :app module
./gradlew :app:connectedDebugAndroidTest

# View results in browser
open app/build/reports/androidTests/connected/index.html
```

### Run Specific Test Class

```bash
# Run PostsJourneyTest
./gradlew :app:connectedDebugAndroidTest --tests "com.alxnophis.jetpack.e2e.PostsJourneyTest"

# Run PostsJourneyRobotTest (recommended)
./gradlew :app:connectedDebugAndroidTest --tests "com.alxnophis.jetpack.e2e.PostsJourneyRobotTest"
```

### Run Single Test Method

```bash
# Run specific test
./gradlew :app:connectedDebugAndroidTest \
  --tests "com.alxnophis.jetpack.e2e.PostsJourneyRobotTest.GIVEN_app_launches_WHEN_navigate_to_posts_THEN_posts_are_displayed"
```

### Run with Android CLI

```bash
# Using android CLI (if available)
android run --type=INSTRUMENTATION_TEST --device=<device-serial>
```

## Test Patterns

### E2E Tests with Screen Robot Pattern (Recommended)

Located in `app/src/androidTest/java/com/alxnophis/jetpack/e2e/`, these tests validate complete user journeys across features:

```kotlin
// PostsJourneyRobotTest.kt - E2E test with Robot pattern
@Test
fun GIVEN_app_WHEN_complete_user_journey_THEN_all_screens_work() {
    // Tests navigation: Home → Posts → Detail → Back
    composeTestRule
        .homeScreen()
        .waitForHomeScreen()
        .navigateToPosts()
        .waitForPostsToLoad()
        .clickPostAtIndex(0)

    composeTestRule
        .postDetailScreen()
        .waitForDetailToLoad()
        .clickBack()
}
```

**Advantages:**

- ✅ Readable and maintainable
- ✅ Reusable screen interactions
- ✅ Fluent API
- ✅ Easy to update when UI changes
- ✅ Located in `:app` where complete journeys belong

### Basic E2E Test Pattern

Simple, straightforward tests using Compose Test APIs directly:

```kotlin
@Test
fun GIVEN_app_launches_WHEN_navigate_to_posts_THEN_posts_are_displayed() {
    // GIVEN
    composeTestRule.waitUntil(timeoutMillis = 5000) {
        composeTestRule
            .onAllNodesWithText("Jetpack")
            .fetchSemanticsNodes()
            .isNotEmpty()
    }

    // WHEN
    composeTestRule
        .onNodeWithText("Posts")
        .performClick()

    // THEN
    composeTestRule
        .onNodeWithText("Posts")
        .assertIsDisplayed()
}
```

**Key Point:** All E2E tests use `createAndroidComposeRule<RootActivity>()` to test the full app context and live in the `:app` module.

## Writing New E2E Tests

### 1. Create Your Test in `:app` Module

E2E tests should live in `app/src/androidTest/java/com/alxnophis/jetpack/e2e/`:

```kotlin
package com.alxnophis.jetpack.e2e

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alxnophis.jetpack.root.ui.RootActivity
import com.alxnophis.jetpack.utils.homeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class YourFeatureJourneyTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<RootActivity>()

    @Test
    fun GIVEN_app_WHEN_action_THEN_result() {
        // Your test here
    }
}
```

### 2. Add Screen Robots if Needed

Create reusable screen interactions in `app/src/androidTest/java/com/alxnophis/jetpack/utils/ScreenRobots.kt`:

```kotlin
class YourFeatureScreenRobot(private val composeTestRule: ComposeTestRule) {
    fun waitForScreen(): YourFeatureScreenRobot {
        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodesWithText("YourFeature")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        return this
    }

    fun performAction(): YourFeatureScreenRobot {
        composeTestRule
            .onNodeWithText("Button")
            .performClick()
        return this
    }
}

fun ComposeTestRule.yourFeatureScreen() = YourFeatureScreenRobot(this)
```

## Test Naming Convention

Follow BDD-style naming:

```
GIVEN_<initial_state>_WHEN_<action>_THEN_<expected_result>
```

Examples:

- `GIVEN_app_launches_WHEN_navigate_to_posts_THEN_posts_are_displayed`
- `GIVEN_posts_screen_WHEN_user_clicks_post_THEN_detail_screen_appears`
- `GIVEN_post_detail_screen_WHEN_user_clicks_back_THEN_returns_to_posts_list`

## Important Testing Guidelines

### 1. Content Descriptions

Add semantic content descriptions to your composables for testing:

```kotlin
@Composable
fun PostItem(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("Post item"),
    ) {
        // Content
    }
}
```

### 2. Wait Strategies

Always use `waitUntil` instead of fixed delays:

```kotlin
// ✅ Good
composeTestRule.waitUntil(timeoutMillis = 5000) {
    composeTestRule
        .onAllNodesWithText("Posts")
        .fetchSemanticsNodes()
        .isNotEmpty()
}

// ❌ Bad
Thread.sleep(5000)
```

### 3. Idempotency

Tests should be independent and repeatable:

- Clean state before each test
- Don't rely on test execution order
- Use `@Before` and `@After` for setup/teardown if needed

### 4. Test Data

For tests requiring specific data:

- Use mock data sources when possible
- Consider creating test-specific flavors
- Use dependency injection to swap implementations

## Troubleshooting

### Tests Fail Due to Missing Content Descriptions

**Problem:** `onNodeWithContentDescription` fails to find nodes

**Solution:** Add semantic content descriptions to your composables:

```kotlin
Modifier.semantics {
    contentDescription = "Description here"
}
```

### Tests Timeout Waiting for UI

**Problem:** `waitUntil` times out

**Solutions:**

1. Increase timeout: `waitUntil(timeoutMillis = 15000)`
2. Check if the UI element actually appears
3. Verify network calls are working (check backend connectivity)
4. Use mock data for more reliable tests

### Device Not Found

**Problem:** `No connected devices!`

**Solutions:**

```bash
# Check devices
adb devices

# Start emulator
android emulator start

# Or use a specific device
adb -s <device-serial> shell
```

### Tests Pass Locally But Fail on CI

**Solutions:**

1. Ensure emulator is properly configured in CI
2. Add longer timeouts for CI environment
3. Disable animations: `adb shell settings put global window_animation_scale 0`

## Advanced Topics

### Testing with Mock Data

Create a test application class:

```kotlin
class TestJetpackApplication : JetpackApplication() {
    override fun onCreate() {
        super.onCreate()
        // Override Koin modules with test implementations
    }
}
```

### Running Tests in Parallel

```bash
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.numShards=3 \
  -Pandroid.testInstrumentationRunnerArguments.shardIndex=0
```

### Continuous Integration

Example GitHub Actions workflow:

```yaml
- name: Run E2E Tests
  run: |
    ./gradlew connectedDebugAndroidTest

- name: Upload Test Results
  uses: actions/upload-artifact@v3
  if: always()
  with:
    name: test-results
    path: app/build/reports/androidTests/
```

## Resources

- [Compose Testing Guide](https://developer.android.com/jetpack/compose/testing)
- [Testing Navigation](https://developer.android.com/guide/navigation/navigation-testing)
- [Espresso Documentation](https://developer.android.com/training/testing/espresso)
- [Testing Best Practices](https://developer.android.com/training/testing/fundamentals)

## Next Steps

1. Add content descriptions to all interactive UI elements
2. Expand test coverage to other features (Authentication, Settings, etc.)
3. Set up CI/CD pipeline for automated E2E testing
4. Consider screenshot testing with Roborazzi for visual regression
5. Add performance benchmarks using Macrobenchmark
