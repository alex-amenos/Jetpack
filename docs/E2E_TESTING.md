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
│   └── PostsJourneyRobotTest.kt         # ⭐ E2E with Robot pattern (recommended)
├── robot/
│   ├── HomeScreenRobot.kt               # Home screen interactions
│   ├── PostsScreenRobot.kt              # Posts list interactions
│   └── PostDetailScreenRobot.kt         # Post detail interactions
└── utils/
    └── ComposeTestRuleExtensions.kt     # Utility extensions (waitUntil)
```

**Key Design Decisions:**
- Screen Robots are separated into individual files per screen for better maintainability
- Each robot provides a fluent API that encapsulates UI interactions
- Tests use `testTag()` instead of `contentDescription` for stable test identifiers
- All test tags are centralized in `CoreTags` for consistency

## Running E2E Tests

### Prerequisites

1. **Device/Emulator**: You need a running Android device or emulator
   ```bash
    # List connected devices
    adb devices
    
    # Or list and start an Android Virtual Device (AVD)
    emulator -list-avds
    emulator -avd <name>
   ```

2. **Dependencies**: All required dependencies are configured in `buildSystem/gradle/common-app-base.gradle`

### Run All E2E Tests

```bash
# Run all E2E tests from :app module
./gradlew :app:connectedDebugAndroidTest

# View results in browser
open app/build/reports/androidTests/connected/debug/index.html
```

### Run Specific Test Class

```bash
# Run PostsJourneyRobotTest (recommended)
./gradlew :app:connectedDebugAndroidTest --tests "com.alxnophis.jetpack.e2e.PostsJourneyRobotTest"
```

### Run Single Test Method

```bash
# Run specific test
./gradlew :app:connectedDebugAndroidTest \
  --tests "com.alxnophis.jetpack.e2e.PostsJourneyRobotTest.GIVEN_app_launches_WHEN_navigate_to_posts_THEN_posts_are_displayed"
```

### Run with adb

```bash
# Run instrumentation tests directly with adb
adb shell am instrument -w com.alxnophis.jetpack.test/androidx.test.runner.AndroidJUnitRunner
```

## Test Patterns

### Screen Robot Pattern (Recommended)

The **Screen Robot Pattern** (also known as Page Object Model) is the recommended approach for E2E testing. It encapsulates UI interactions into reusable, maintainable robot classes.

**Benefits:**
- ✅ **Readable**: Tests read like user stories with fluent API
- ✅ **Maintainable**: UI changes only require updating the robot
- ✅ **Reusable**: Robots are shared across multiple test cases
- ✅ **Encapsulated**: Implementation details hidden from tests
- ✅ **Scalable**: Easy to add new screens and interactions

**Architecture:**
```
Test Class → Screen Robot → Compose Test API → Production UI
```

Located in `app/src/androidTest/java/com/alxnophis/jetpack/`:

```kotlin
// PostsJourneyRobotTest.kt - E2E test with Robot pattern
@Test
fun GIVEN_app_WHEN_complete_user_journey_THEN_all_screens_work() {
    // Tests navigation: Home → Posts → Detail → Back
    composeTestRule
        .homeScreen()                    // HomeScreenRobot
        .waitForHomeScreen()
        .assertHomeDisplayed()
        .navigateToPosts()               // Returns PostsScreenRobot

    composeTestRule
        .postsScreen()
        .waitForPostsToLoad()
        .assertPostsDisplayed()
        .clickPostAtIndex(0)             // Navigate to detail

    composeTestRule
        .postDetailScreen()              // PostDetailScreenRobot
        .waitForDetailToLoad()
        .assertDetailDisplayed()
        .clickBack()                     // Returns to posts

    composeTestRule
        .postsScreen()
        .assertPostItemsVisible()
}
```

**Available Screen Robots:**

#### HomeScreenRobot
```kotlin
composeTestRule.homeScreen()
    .waitForHomeScreen()              // Wait for home to load
    .assertHomeDisplayed()            // Verify home is visible
    .navigateToPosts()                // Navigate to posts (returns PostsScreenRobot)
```

#### PostsScreenRobot
```kotlin
composeTestRule.postsScreen()
    .waitForPostsToLoad()             // Wait for posts (15s timeout)
    .assertPostsDisplayed()           // Verify posts screen title
    .assertPostItemsVisible()         // Verify posts list visible
    .scrollToIndex(5)                 // Scroll to specific post
    .clickPostAtIndex(0)              // Click on a post
    .performPullToRefresh()           // Pull-to-refresh gesture
```

#### PostDetailScreenRobot
```kotlin
composeTestRule.postDetailScreen()
    .waitForDetailToLoad()            // Wait for detail (5s timeout)
    .assertDetailDisplayed()          // Verify detail content visible
    .clickBack()                      // Navigate back (returns PostsScreenRobot)
```

### Test Tag Strategy

Tests use `testTag()` instead of `contentDescription` for element identification.

**Why testTag?**
- Decouples tests from accessibility strings
- Allows `contentDescription` to remain user-focused and localized
- Provides stable test identifiers independent of UI text changes

**CoreTags Reference:**

All test tags are centralized in `CoreTags`:

| Tag | Purpose | Production Location |
|-----|---------|-------------------|
| `TAG_POST_ITEM` | Individual post in list | `PostScreen.kt:207` |
| `TAG_POSTS_LIST` | Posts list container | `PostScreen.kt:128` |
| `TAG_POST_DETAIL` | Post detail content | `PostDetailScreen.kt:170` |
| `TAG_CORE_BACK` | Back navigation button | Multiple screens |

**Production Code Example:**
```kotlin
// PostScreen.kt
LazyColumn(
    modifier = Modifier.testTag(CoreTags.TAG_POSTS_LIST)  // ✅ testTag for tests
) {
    items(posts) { post ->
        PostCard(
            post = post,
            modifier = Modifier
                .testTag(CoreTags.TAG_POST_ITEM)          // ✅ testTag for tests
                .semantics {
                    contentDescription = "Post by ${post.author}"  // ✅ For accessibility
                }
        )
    }
}
```

**Test Code Example:**
```kotlin
// PostsScreenRobot.kt
composeTestRule
    .onNodeWithTag(CoreTags.TAG_POSTS_LIST)  // ✅ Using testTag
    .performScrollToIndex(5)

composeTestRule
    .onAllNodesWithTag(CoreTags.TAG_POST_ITEM)[0]  // ✅ Using testTag
    .performClick()
```

## Writing New E2E Tests

### 1. Create Your Test in `:app` Module

E2E tests should live in `app/src/androidTest/java/com/alxnophis/jetpack/e2e/`:

```kotlin
package com.alxnophis.jetpack.e2e

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alxnophis.jetpack.robot.homeScreen
import com.alxnophis.jetpack.root.ui.RootActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class YourFeatureJourneyTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<RootActivity>()

    @Test
    fun GIVEN_initial_state_WHEN_action_THEN_expected_result() {
        composeTestRule
            .homeScreen()
            .waitForHomeScreen()
            .navigateToYourFeature()
            // ... continue test
    }
}
```

### 2. Create Screen Robot

Create a new robot in `app/src/androidTest/java/com/alxnophis/jetpack/robot/`:

```kotlin
package com.alxnophis.jetpack.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.alxnophis.jetpack.core.ui.composable.CoreTags

/**
 * Screen Robot for Your Feature Screen
 *
 * Provides fluent API for testing your feature.
 */
class YourFeatureScreenRobot(
    private val composeTestRule: ComposeTestRule,
) {
    /**
     * Waits for the screen to load.
     */
    fun waitForScreen(timeoutMillis: Long = 5000): YourFeatureScreenRobot {
        composeTestRule.waitUntil(timeoutMillis) {
            composeTestRule
                .onAllNodesWithTag(CoreTags.TAG_YOUR_FEATURE)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        return this
    }

    /**
     * Performs an action on the screen.
     */
    fun performAction(): YourFeatureScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_YOUR_FEATURE_BUTTON)
            .performClick()
        return this
    }

    /**
     * Asserts the screen is displayed.
     */
    fun assertScreenDisplayed(): YourFeatureScreenRobot {
        composeTestRule
            .onNodeWithTag(CoreTags.TAG_YOUR_FEATURE)
            .assertIsDisplayed()
        return this
    }
}

/**
 * Extension function to create a YourFeatureScreenRobot.
 */
fun ComposeTestRule.yourFeatureScreen(): YourFeatureScreenRobot = 
    YourFeatureScreenRobot(this)
```

### 3. Add Test Tags to Production Code

In your feature's composable (e.g., `feature/yourfeature/src/main/java/.../YourFeatureScreen.kt`):

```kotlin
import com.alxnophis.jetpack.core.ui.composable.CoreTags

@Composable
internal fun YourFeatureScreen() {
    Column(
        modifier = Modifier
            .testTag(CoreTags.TAG_YOUR_FEATURE)  // ✅ Add testTag
    ) {
        Button(
            modifier = Modifier.testTag(CoreTags.TAG_YOUR_FEATURE_BUTTON),
            onClick = { /* ... */ }
        ) {
            Text("Action")
        }
    }
}
```

### 4. Define Tags in CoreTags

In `shared/core/src/main/java/.../CoreTags.kt`:

```kotlin
object CoreTags {
    // Existing tags...
    const val TAG_CORE_BACK = "core_back"
    const val TAG_POSTS_LIST = "posts_list"
    
    // Your new feature tags
    const val TAG_YOUR_FEATURE = "your_feature"
    const val TAG_YOUR_FEATURE_BUTTON = "your_feature_button"
}
```

### 5. Use in Tests

```kotlin
@Test
fun GIVEN_app_WHEN_user_performs_action_THEN_result_displayed() {
    composeTestRule
        .homeScreen()
        .waitForHomeScreen()
        .navigateToYourFeature()
    
    composeTestRule
        .yourFeatureScreen()
        .waitForScreen()
        .assertScreenDisplayed()
        .performAction()
}
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

### 1. Use Test Tags (Not Content Descriptions)

**✅ Recommended: Use testTag for test identification**
```kotlin
@Composable
fun PostItem(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(CoreTags.TAG_POST_ITEM)  // ✅ For tests
            .semantics {
                contentDescription = "Post by ${post.author}"  // ✅ For accessibility
            }
    ) {
        // Content
    }
}
```

**❌ Avoid: Using contentDescription for tests**
```kotlin
// Don't do this - couples tests to accessibility strings
Card(
    modifier = Modifier.semantics {
        contentDescription = "Post item"  // ❌ Hard to localize, couples test to a11y
    }
)

// Test code becomes fragile
composeTestRule
    .onNodeWithContentDescription("Post item")  // ❌ Breaks if text changes or is localized
```

**Why testTag?**
- Separates testing concerns from accessibility
- Allows localization of accessibility strings
- Provides stable identifiers independent of UI text
- Centralized in `CoreTags` for consistency

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

### Tests Fail to Find UI Elements

**Problem:** `onNodeWithTag` or `onNodeWithText` fails to find nodes

**Solutions:**

1. **Verify testTag is added to production code:**
   ```kotlin
   // Check your composable has testTag
   Modifier.testTag(CoreTags.TAG_YOUR_ELEMENT)
   ```

2. **Check CoreTags definition:**
   ```kotlin
   // Ensure tag is defined in CoreTags
   object CoreTags {
       const val TAG_YOUR_ELEMENT = "your_element"
   }
   ```

3. **Use correct test API:**
   ```kotlin
   // ✅ For testTag
   composeTestRule.onNodeWithTag(CoreTags.TAG_YOUR_ELEMENT)
   
   // ✅ For text content
   composeTestRule.onNodeWithText("Button Text")
   
   // ❌ Don't use contentDescription for tests
   composeTestRule.onNodeWithContentDescription("...") // Avoid
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

### Official Documentation
- [Compose Testing Guide](https://developer.android.com/jetpack/compose/testing)
- [Testing Navigation](https://developer.android.com/guide/navigation/navigation-testing)
- [Testing Best Practices](https://developer.android.com/training/testing/fundamentals)
- [Semantics in Compose](https://developer.android.com/jetpack/compose/semantics)

### Testing Patterns
- [Screen Robot Pattern by Jake Wharton](https://jakewharton.com/testing-robots/)
- [Page Object Model](https://martinfowler.com/bliki/PageObject.html)
- [BDD Test Naming](https://dannorth.net/introducing-bdd/)

### Project Documentation
- `app/src/androidTest/README.md` - Detailed E2E testing guide
- `AGENTS.md` - Project conventions and test commands
- `CoreTags.kt` - Centralized test tag definitions

## Next Steps

### Immediate Actions
1. ✅ **Screen Robot Pattern** - All E2E tests now use Screen Robot pattern
2. ✅ **testTag Strategy** - Tests use `CoreTags` for stable identifiers
3. ✅ **Separated Robots** - Each screen has its own robot file
4. ✅ **Comprehensive Documentation** - KDoc and README in place

### Future Improvements
1. **Expand Coverage**: Add E2E tests for other features
   - Authentication journey
   - Settings configuration
   - File downloader flows
   - Location tracker scenarios

2. **CI/CD Integration**: Set up automated E2E testing
   - Configure GitHub Actions / CI pipeline
   - Add test result reporting
   - Set up test failure notifications

3. **Visual Testing**: Add screenshot testing
   - Integrate Roborazzi for visual regression
   - Generate screenshot baselines
   - Compare UI changes automatically

4. **Performance**: Add benchmarking
   - Use Macrobenchmark for startup metrics
   - Measure frame rendering performance
   - Track memory usage during journeys

5. **Test Data Management**:
   - Create test-specific data factories
   - Implement mock data repositories
   - Add database seeding for consistent tests
