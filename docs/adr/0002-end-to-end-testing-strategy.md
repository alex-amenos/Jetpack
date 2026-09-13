# ADR 0002: End-to-End Testing Strategy & Architecture

- **Status:** Accepted
- **Date:** 2026-09-13
- **Deciders:** Architecture Team

---

## Context

The Jetpack application is structured as a multi-module project where `:feature:*` modules are independent, vertical slices that must not depend on each other. However, validating critical user journeys requires executing flows that cross multiple features (e.g., Home → Posts list → Post detail → Back navigation) against `RootActivity`.

Additionally, Jetpack Compose UI tests can easily become brittle if they depend on localized user-facing strings or accessibility properties (`contentDescription`), or if interactions are duplicated across multiple test classes without abstraction.

---

## Decision

1. **Locate E2E Tests in the `:app` Module:**
   - All end-to-end and cross-feature journey tests live in `app/src/androidTest/`.
   - Feature modules host isolated unit, behavior, and screenshot tests (Roborazzi), but never depend on other feature modules to test multi-screen journeys.
   - The `:app` module already has access to all feature dependencies and `RootActivity`, eliminating circular dependency risks.

2. **Adopt the Screen Robot Pattern (Page Object Model):**
   - UI interactions and assertions are encapsulated into dedicated, fluent robot classes per screen under `app/src/androidTest/java/com/alxnophis/jetpack/robot/` (e.g., `HomeScreenRobot`, `PostsScreenRobot`, `PostDetailScreenRobot`).
   - Tests read as declarative user stories:
     ```kotlin
     composeTestRule
         .homeScreen()
         .waitForHomeScreen()
         .navigateToPosts()

     composeTestRule
         .postsScreen()
         .waitForPostsToLoad()
         .clickPostAtIndex(0)

     composeTestRule
         .postDetailScreen()
         .waitForDetailToLoad()
         .assertDetailDisplayed()
         .clickBack()
     ```

3. **Centralized Test Tag Strategy (`CoreTags`):**
   - Tests identify nodes strictly using `Modifier.testTag(CoreTags.<TAG_NAME>)`.
   - All test tags are defined centrally in `shared/core/.../CoreTags.kt` (e.g., `TAG_POSTS_LIST`, `TAG_POST_ITEM`, `TAG_CORE_BACK`).
   - `contentDescription` is reserved strictly for accessibility and localization, decoupling test stability from UI copy changes.

4. **Deterministic Waiting over Fixed Delays:**
   - Synchronize asynchronous operations using `ComposeTestRule.waitUntil { ... }` rather than `Thread.sleep` or arbitrary timeouts.

5. **BDD Naming Convention:**
   - Follow standard BDD naming format:
     `GIVEN_<initial_state>_WHEN_<action>_THEN_<expected_result>`

---

## Execution Guide

### Running E2E Tests
```bash
# Run all E2E tests from :app module on a connected device/emulator
./gradlew :app:connectedDebugAndroidTest

# Run a specific journey test class
./gradlew :app:connectedDebugAndroidTest --tests "com.alxnophis.jetpack.e2e.PostsJourneyRobotTest"

# Run a specific test method
./gradlew :app:connectedDebugAndroidTest \
  --tests "com.alxnophis.jetpack.e2e.PostsJourneyRobotTest.GIVEN_app_launches_WHEN_navigate_to_posts_THEN_posts_are_displayed"
```

---

## Consequences

### Positive
- **Module Architecture Integrity:** Preserves the rule that feature modules remain isolated and decoupled from each other.
- **Decoupled Accessibility:** Localization and accessibility descriptions can change without breaking UI test assertions.
- **Maintainability & Reusability:** UI refactors only require updating the corresponding Screen Robot rather than multiple test suites.
- **Readable User Journeys:** Tests serve as living functional documentation of application workflows.

### Negative / Trade-offs
- E2E tests require an active emulator or physical device, resulting in longer execution times than JVM unit or Robolectric tests.
- Centralizing `CoreTags` in `:shared:core` requires care to avoid tag bloat or accidental duplication across unrelated screens.
