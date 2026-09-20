# Kotlin 2.4 Migration & Feature Adoption Plan

## 1. Objectives
- Upgrade project Kotlin toolchain from `2.2.21` to the latest stable **Kotlin 2.4** (`2.4.20`).
- Update tightly coupled compiler plugins and companion libraries (`google-ksp`, `arrow-stack`, `koin`, `ktlint`).
- Integrate and demonstrate new Kotlin 2.4 features in active production code:
  - **Explicit Backing Fields** in state management (`:feature:file-downloader`).
  - **Context Parameters** for ambient dependency passing (`:feature:authentication`).
- Catalog future candidate use cases for broader Context Parameters adoption across architectural layers.
- Verify build, ktlint, tests, coverage, and screenshot regression suites.

---

## 2. Dependency Matrix

| Component | Current Version | Target Version | Status | Rationale |
| :--- | :--- | :--- | :--- | :--- |
| **Kotlin** (`kotlin`) | `2.2.21` | **`2.4.20`** | **Done** | Latest stable release. Automatically upgrades Kotlin Gradle Plugin, Compose Compiler (`org.jetbrains.kotlin.plugin.compose`), Parcelize, Serialization, and test runtime. |
| **KSP** (`google-ksp`) | `2.3.6` | **`2.3.12`** | **Done** | Built for Kotlin 2.4 with explicit backing field AST support (`KSBackingField`). |
| **Arrow Stack** (`arrow-stack`) | `2.2.2.1` | **`2.2.3`** | **Done** | Latest stable version for Optics and Arrow KSP code generation. |
| **Koin** (`koin-*`) | `4.2.1` | **`4.2.2`** | **Done** | Minor patch update compatible with Kotlin 2.4. |
| **Ktlint Engine** | `1.5.0` | **`1.8.0`** | **Done** | Configured in `ktlint.gradle` for native parsing and formatting of Kotlin 2.4 context parameter syntax. |
| **kotlinx.coroutines** | `1.11.0` | `1.11.0` | **Retained** | Fully compatible. |
| **kotlinx.serialization** | `1.11.0` | `1.11.0` | **Retained** | Fully compatible. |
| **Android Gradle Plugin** | `9.4.1` | `9.4.1` | **Retained** | Compatible with Gradle 9.7.1 and Kotlin 2.4. |
| **Gradle Wrapper** | `9.7.1` | `9.7.1` | **Retained** | Retained at Gradle 9.7.1. |

---

## 3. Compiler Configuration

In Kotlin 2.4 (Language Version 2.4), **Context Parameters** and **Explicit Backing Fields** are active by default. Compiler options in `buildSystem/gradle/common-android-base.gradle` and `buildSystem/gradle/common-app-base.gradle` remain clean without redundant feature flags:

```groovy
kotlin.compilerOptions {
    freeCompilerArgs = ['-Xstring-concat=inline']
}
```

---

## 4. Implementation Steps & Completed Work

### Phase 1: Dependency & Build Configuration (Completed)
1. Updated `gradle/libs.versions.toml`:
   - `kotlin = "2.4.20"`
   - `google-ksp = "2.3.12"`
   - `arrow-stack = "2.2.3"`
   - `koin-* = "4.2.2"`
2. Updated Ktlint engine version to `1.8.0` in `buildSystem/gradle/ktlint.gradle`.
3. Executed `./gradlew compileDebugKotlin` to validate baseline compilation.

### Phase 2: Feature Adoption — Explicit Backing Fields (Completed)
- **Target**: `FileDownloaderRepositoryImpl.kt` (`feature:file-downloader`).
  - Replaced legacy private backing properties ceremony (`_downloadingFiles` and `_downloadedFiles`) with explicit backing fields:
    ```kotlin
    override val downloadingFiles: StateFlow<List<DownloaderFile>>
        field = MutableStateFlow(emptyList())

    override val downloadedFiles: StateFlow<List<DownloaderFile>>
        field = MutableStateFlow(emptyList())
    ```
  - Mutates directly within repository functions (`downloadingFiles.update { ... }`).
  - Eliminates boilerplate while guaranteeing strict read-only `StateFlow` encapsulation to consumers.
- **Verification**: All unit tests in `:feature:file-downloader` passed.

### Phase 3: Feature Adoption — Context Parameters (Completed)
- **Target 1**: `AuthenticateUseCase.kt` (`feature:authentication`).
  - Replaced constructor injection of `CoroutineDispatcher` with ambient context parameter:
    ```kotlin
    internal class AuthenticateUseCase(
        private val delay: Long = DELAY,
    ) {
        context(dispatcher: CoroutineDispatcher)
        suspend operator fun invoke(
            email: String,
            password: String,
        ): Either<AuthenticationError, Authenticated> =
            withContext(dispatcher) { ... }
    }
    ```
  - Caller (`AuthenticationViewModel.kt`) supplies context at execution site: `context(ioDispatcher) { authenticateUseCase(email, password) }`.
  - Unit tests (`AuthenticateUseCaseUnitTest.kt` and `AuthenticationViewModelUnitTest.kt`) supply `context(testDispatcher)` directly.
- **Target 2**: `GetNavigationItemsUseCase.kt` (`feature:home`).
  - Replaced constructor dispatcher injection with `context(dispatcher: CoroutineDispatcher) suspend operator fun invoke()`.
  - Caller (`HomeViewModel.kt`) supplies context: `context(ioDispatcher) { getNavigationItemsUseCase() }`.
  - Added unit test suite in `GetNavigationItemsUseCaseTest.kt` and `HomeViewModelTest.kt`.
- **Target 3**: `BallClickerTimerUseCase.kt` (`feature:game:ballclicker`).
  - Replaced constructor dispatcher injection with `context(dispatcher: CoroutineDispatcher) operator fun invoke(...)`.
  - Caller (`BallClickerViewModel.kt`) supplies context: `context(defaultDispatcher) { ballClickerTimerUseCase(...) }`.
  - Added unit test suite in `BallClickerTimerUseCaseTest.kt` and `BallClickerViewModelTest.kt`.
- **Tooling Support**:
  - Updated Ktlint engine to `1.8.0` in `buildSystem/gradle/ktlint.gradle` for full syntax and AST support.
- **Verification**: All unit tests in `:feature:authentication`, `:feature:home`, and `:feature:game:ballclicker` passed.

---

## 5. Candidate Use Cases for Future Context Parameters Adoption

Beyond the completed use cases, additional codebase opportunities exist:

### A. Data Layer: Infrastructure Decoupling in Repositories & Data Sources
- **`FileDownloaderRepositoryImpl`** (`:feature:file-downloader`):
  - Current: Retains `ioDispatcher: CoroutineDispatcher` in constructor solely to wrap download calls.
  - Future: Declare `context(dispatcher: CoroutineDispatcher) override suspend fun downloadFile(fileUrl: String)`.
- **`PostsRemoteDataSourceImp`** (`:feature:posts`):
  - Current: `CallError.mapToError` takes 4 separate parameters for error mappings.
  - Future: Define `interface ErrorMappingContext<T> { val noConnectivity: T; val unexpected: T; val server: T; val network: T }` and declare `context(mapping: ErrorMappingContext<T>) fun <T> CallError.mapToDomainError(): T`.

### C. Shared Utilities: Contextual Localization & Formatting
- **`BaseDateFormatter`** (`:shared:core`):
  - Current: Class instance storing `private val locale: Locale`.
  - Future: Top-level extension functions requiring ambient `Locale`:
    ```kotlin
    context(locale: Locale)
    fun Date.formatToReadableDateTime(): String =
        SimpleDateFormat(PATTERN_READABLE_DATE_TIME, locale).format(this)
    ```

---

## 6. Verification & Validation Quality Gates

All verification suites executed and passed:

1. **Formatting**:
   ```bash
   ./gradlew ktlintCheck
   ```
2. **Linting**:
   ```bash
   ./gradlew lintDebug
   ```
3. **Unit Tests & Code Coverage**:
   ```bash
   ./gradlew testDebugUnitTest
   ./gradlew koverHtmlReportDebug
   ```
4. **Screenshot Verification**:
   ```bash
   ./gradlew verifyRoborazziDebug
   ```
