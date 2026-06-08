# AGENTS.md - AI Coding Agent Guidelines

Multi-module Android app using Kotlin, Jetpack Compose, and MVI architecture with Arrow for functional programming.

**Tech Stack:** Kotlin 2.2.21 | Compose BOM 2026.05.01 | Koin 4.2.1 | Arrow-kt 2.2.2.1 | Retrofit | JUnit 5 | Roborazzi

## High-Signal Workflows & Commands

**CI Validation (Run before committing):**
```bash
./gradlew ktlintCheck                      # Run linting
./gradlew koverHtmlReportDebug             # Run all unit tests + coverage (used in CI)
```

**Testing & Screenshots:**
- **Run module test:** `./gradlew :feature:posts:testDebugUnitTest`
- **Single test class:** `./gradlew :feature:posts:testDebugUnitTest --tests "*.PostsViewModelUnitTests"`
- **Screenshot tests (Roborazzi):**
  - Verify: `./gradlew verifyRoborazziDebug`
  - Record new baselines: `./gradlew recordRoborazziDebug`

**Formatting (Auto-fix):**
```bash
./gradlew ktlintFormat
```

## Architecture & Code Conventions

### MVI & Functional Programming
- **BaseViewModel:** ViewModels must extend `com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel<Event, State>`. Override `handleEvent(event: Event)` to process actions.
- **Error Handling:** Use Arrow's `Either<Error, Data>`. Domain errors are modeled as sealed interfaces (e.g., `PostsError`). Avoid throwing exceptions for business logic; use `.mapLeft` / `.fold`.
- **State Management:** Use Arrow Optics (`@optics` + `updateCopy`) for complex immutable state updates in ViewModels, preferring this over standard data class `copy()` methods.
  ```kotlin
  _uiState.updateCopy {
      PostsUiState.status set PostsStatus.Loading
      PostsUiState.posts set posts.toImmutableList()
  }
  ```
- **Testing:** 
  - Test flow state emissions using **Turbine** (`viewModel.uiState.test { awaitItem() shouldBeEqualTo ... }`).
  - Use BDD naming conventions for test methods: ``@Test fun `GIVEN ... WHEN ... THEN ...`()``.
  - Use the Object Mother pattern (e.g., `PostMother`) for creating test fixtures.

### Project Structure (Module Types)
The application is structured into three main types of modules to enforce separation of concerns:
- **`:app`**: The application shell. Wires everything together, initializes DI (Koin), and hosts the root navigation graph. Should contain minimal business logic.
- **`:feature:*`**: Independent, vertical feature slices (e.g., `:feature:posts`, `:feature:authentication`). Each contains its own UI, Domain, and Data layers. **Rule:** Feature modules should generally not depend on other feature modules.
- **`:shared:*`**: Foundational, horizontal libraries (e.g., `:shared:core`, `:shared:api`, `:shared:testing`). Contains cross-cutting concerns, base classes, networking setup, and UI theme components used by features.

### Visibility Restrictions
  - ViewModels, UI components, Use Cases, and Repository Implementations must be `internal`.
  - Repository interfaces can be `public` to serve as a cross-module API.

### Style
- **Line length:** ~150 chars (not strictly enforced for `*.kt`/`*.kts`).
- **Imports:** No wildcard imports. Group imports alphabetically:
  1. Android (`android.*`, `androidx.*`)
  2. Third-party (`arrow.*`, `kotlinx.*`, `org.koin.*`, etc.)
  3. Project imports (`com.alxnophis.jetpack.*`)
- **Naming:** 
  - `ktlint_function_naming_ignore_when_annotated_with = Composable`
  - ViewModels: `{Feature}ViewModel`
  - Repositories: `{Feature}Repository` / `{Feature}RepositoryImpl`
  - UseCases: `{Action}{Entity}UseCase`
