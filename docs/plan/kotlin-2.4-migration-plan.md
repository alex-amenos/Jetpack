# Kotlin 2.4 Migration & Feature Adoption Plan

## 1. Objectives
- Upgrade project Kotlin toolchain from `2.2.21` to the latest stable **Kotlin 2.4** (`2.4.20`).
- Update tightly coupled compiler plugins and companion libraries (`google-ksp`, `arrow-stack`, `koin`).
- Configure Kotlin compiler arguments for new language features (`-Xcontext-parameters`, `-Xexplicit-backing-fields`).
- Integrate and demonstrate new Kotlin 2.4 features:
  - **Explicit Backing Fields** in state management (`:feature:file-downloader`).
- Verify build, ktlint, tests, coverage, and screenshot regression suites.

---

## 2. Dependency Matrix

| Component | Current Version | Target Version | Rationale |
| :--- | :--- | :--- | :--- |
| **Kotlin** (`kotlin`) | `2.2.21` | **`2.4.20`** | Latest stable release. Updates Kotlin Gradle Plugin, Compose Compiler (`org.jetbrains.kotlin.plugin.compose`), Parcelize, Serialization, and test runtime. |
| **KSP** (`google-ksp`) | `2.3.6` | **`2.3.12`** | Required for Kotlin 2.4 and explicit backing fields support (`KSBackingField`). |
| **Arrow Stack** (`arrow-stack`) | `2.2.2.1` | **`2.2.3`** | Latest stable version for Optics and Arrow KSP code generation. |
| **Koin** (`koin-*`) | `4.2.1` | **`4.2.2`** | Minor patch update compatible with Kotlin 2.4. |
| **kotlinx.coroutines** | `1.11.0` | `1.11.0` | Retained (compatible). |
| **kotlinx.serialization** | `1.11.0` | `1.11.0` | Retained (compatible). |
| **Android Gradle Plugin** | `9.4.1` | `9.4.1` | Retained (compatible with Gradle 9.7.1 and Kotlin 2.4). |
| **Gradle Wrapper** | `9.7.1` | `9.7.1` | Retained. |

---

## 3. Compiler Configuration

Enable new language features in common Gradle build logic (`buildSystem/gradle/common-android-base.gradle` and `buildSystem/gradle/common-app-base.gradle`):

```groovy
kotlin.compilerOptions {
    freeCompilerArgs = [
        '-Xstring-concat=inline',
        '-Xcontext-parameters',
        '-Xexplicit-backing-fields'
    ]
}
```

---

## 4. Implementation Steps

### Phase 1: Dependency & Build Configuration
1. Update `gradle/libs.versions.toml`:
   - `kotlin = "2.4.20"`
   - `google-ksp = "2.3.12"`
   - `arrow-stack = "2.2.3"`
   - `koin-* = "4.2.2"`
2. Add `-Xcontext-parameters` and `-Xexplicit-backing-fields` to compiler options in:
   - `buildSystem/gradle/common-android-base.gradle`
   - `buildSystem/gradle/common-app-base.gradle`
3. Execute `./gradlew compileDebugKotlin` to validate baseline compilation.

### Phase 2: Feature Adoption — Explicit Backing Fields
- **Target**: `FileDownloaderRepositoryImpl.kt` (`feature:file-downloader`).
  - Eliminate ceremony of paired private backing properties:
    ```kotlin
    override val downloadingFiles: StateFlow<List<DownloaderFile>>
        field = MutableStateFlow(emptyList())

    override val downloadedFiles: StateFlow<List<DownloaderFile>>
        field = MutableStateFlow(emptyList())
    ```
  - Mutate directly within repository functions (`downloadingFiles.update { ... }`).
  - Verifies read-only StateFlow encapsulation with zero boilerplate backing properties.
- **Verification**: Run unit tests in `:feature:file-downloader`.

---

## 5. Verification & Validation Quality Gates

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
