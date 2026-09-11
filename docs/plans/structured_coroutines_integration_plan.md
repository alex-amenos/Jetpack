# Integration Plan: Structured Coroutines Toolkit

This document outlines the evaluation, pros and cons, and implementation roadmap for integrating the [`santimattius/structured-coroutines`](https://github.com/santimattius/structured-coroutines) library into this project.

---

## 1. Executive Summary & Recommendation

- **Verdict:** **Proceed with Android Lint rules (`structured-coroutines-lint-rules`) only.**
- **Defer:** The **Kotlin Compiler Plugin (`io.github.santimattius.structured-coroutines`)** and **Detekt rules**.
- **Rationale:** 
  - The Android Lint rules run on Android's standard UAST tooling, carrying **zero compiler ABI break risk** and integrating natively with the project's existing lint setup (`common-android-compose.gradle` already uses `slack-lint-compose`).
  - The Kotlin Compiler Plugin targets **Kotlin 2.4.0 / 2.3.x**, while this repository is built on **Kotlin 2.2.21**. Kotlin K2/FIR compiler plugins are tightly coupled to the internal compiler embeddable ABI, making cross-version plugin usage unstable.
  - The project standardizes on **ktlint**, not Detekt; adding Detekt would introduce unnecessary tooling fragmentation.

---

## 2. Pros and Cons Analysis

### Pros (Why it is a good idea)

1. **Targets Real Coroutine Anti-Patterns Found in This Codebase:**
   - **`BallClickerViewModel.kt:30` (`ViewModelScopeLeak` / `InlineCoroutineScope`):**
     Creates `private val timerScope = CoroutineScope(defaultDispatcher + SupervisorJob())` inside a ViewModel, never cancels it in `onCleared()`, and bypasses `viewModelScope`.
   - **`PostsLocalDataSourceImpl.kt:33-90` (`CancellationExceptionSwallowed`):**
     Multiple `try { ... } catch (e: Exception)` blocks in suspend functions catch `CancellationException`, silently breaking structured coroutine cancellation.
   - **`HomeViewModel.kt:53, 77` & `LocationTrackerViewModel.kt:92, 108` (`RedundantLaunchInCoroutineScope`):**
     Redundant nested `viewModelScope.launch` invocations triggered from inside code paths already running in coroutines.
2. **Native Fit with Existing Infrastructure:**
   - The project already applies custom Android Lint rules (`lintChecks libs.slack.lint.compose`). Adding `structured-coroutines-lint-rules` follows the exact same pattern without requiring extra build plugins.
3. **Framework & Compose Aware:**
   - Automatically understands `viewModelScope`, `lifecycleScope`, and Compose's `rememberCoroutineScope()` without needing explicit annotations.
4. **Zero Runtime Footprint:**
   - Analysis executes during static checks and builds; zero additional bytes added to final release APKs/AABs.
5. **Thread Safety & UI Jank Prevention:**
   - Rules like `MainDispatcherMisuse` flag blocking calls (e.g. synchronous I/O or `Thread.sleep`) invoked on `Dispatchers.Main`.

### Cons & Risks (Why caution is warranted)

1. **Compiler Plugin ABI Fragility:**
   - K2 FIR compiler plugins require exact version alignment with `kotlin-compiler-embeddable`. Upgrading Kotlin in the future would be blocked whenever third-party compiler plugins lag behind.
2. **Maintenance / "Bus Factor":**
   - The library is predominantly maintained by a single author. Relying on it for compilation enforcement introduces upstream dependency risk.
3. **Heuristics & Potential False Positives:**
   - Rules such as `LoopWithoutYield`, `ScopeReuseAfterCancel`, and `UnstructuredLaunch` rely on syntactic heuristics.
   - Legitimate patterns (such as injecting an application-lifetime scope into `PostsRepositoryImpl` for background work) require suppression or annotations if rules are too strict.
4. **Lint Build Overhead:**
   - Multi-module Android Lint is resource-intensive. Running full lint on every local build slows down developer feedback loops unless scoped to CI or pre-push checks.

---

## 3. Architecture & Artifact Breakdown

| Component | Target Platform | Evaluation for This Project | Recommendation |
|-----------|-----------------|-----------------------------|----------------|
| **Android Lint Rules** (`structured-coroutines-lint-rules`) | Android / Compose | Native fit; decoupled from Kotlin compiler internals | **Adopt** |
| **Annotations** (`structured-coroutines-annotations`) | Multiplatform / Android | Provides `@StructuredScope` for DI injected scopes | **Adopt (as needed)** |
| **Kotlin Compiler Plugin** (`structured-coroutines-compiler`) | Kotlin K2 / FIR | Mismatched Kotlin version (Repo: 2.2.21 vs Lib: 2.4.0) | **Defer** |
| **Detekt Rules** (`structured-coroutines-detekt-rules`) | Multiplatform | Repo uses ktlint, not Detekt | **Skip** |
| **IntelliJ Plugin** | IDE | Real-time warnings in Android Studio | **Optional (Developer opt-in)** |

---

## 4. Phased Implementation Roadmap

```
┌─────────────────────────────────────────────────────────────┐
│ Phase 1: Dependency Catalog & Android Lint Setup            │
│ - Add artifact coordinates to libs.versions.toml            │
│ - Wire lintChecks into common-android-base.gradle           │
│ - Configure severities in lint.xml                          │
├─────────────────────────────────────────────────────────────┤
│ Phase 2: Refactor Known Anti-Patterns                       │
│ - Fix BallClickerViewModel unmanaged CoroutineScope         │
│ - Fix PostsLocalDataSourceImpl CancellationException catch  │
│ - Remove redundant nested viewModelScope.launch calls       │
├─────────────────────────────────────────────────────────────┤
│ Phase 3: CI Integration & Verification                      │
│ - Run ./gradlew lintDebug and verify reports                │
│ - Add lint check step to GitHub Actions workflow            │
├─────────────────────────────────────────────────────────────┤
│ Phase 4 (Future): Re-evaluate Compiler Plugin               │
│ - Consider compiler plugin when upgrading to Kotlin 2.4+    │
└─────────────────────────────────────────────────────────────┘
```

### Phase 1: Dependency Catalog & Android Lint Setup

1. **Add to `gradle/libs.versions.toml`:**
   ```toml
   [versions]
   structured-coroutines = "1.1.1"

   [libraries]
   structured-coroutines-lint = { module = "io.github.santimattius:structured-coroutines-lint-rules", version.ref = "structured-coroutines" }
   structured-coroutines-annotations = { module = "io.github.santimattius:structured-coroutines-annotations", version.ref = "structured-coroutines" }
   ```

2. **Add to `buildSystem/gradle/common-android-base.gradle`:**
   ```groovy
   dependencies {
       implementation libs.kotlin.parcelize.runtime
       lintChecks libs.structured.coroutines.lint
   }
   ```

3. **Configure `lint.xml` at Project Root:**
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <lint>
       <!-- Error-level issues -->
       <issue id="GlobalScopeUsage" severity="error" />
       <issue id="InlineCoroutineScope" severity="error" />
       <issue id="ViewModelScopeLeak" severity="error" />
       <issue id="MainDispatcherMisuse" severity="error" />
       <issue id="RunBlockingInSuspend" severity="error" />

       <!-- Warning-level issues (review & migrate) -->
       <issue id="CancellationExceptionSwallowed" severity="warning" />
       <issue id="LifecycleAwareFlowCollection" severity="warning" />
       <issue id="RedundantLaunchInCoroutineScope" severity="warning" />
       <issue id="LoopWithoutYield" severity="ignore" />
   </lint>
   ```

### Phase 2: Refactor Existing Flagged Issues

1. **`BallClickerViewModel.kt`**:
   - Remove `timerScope = CoroutineScope(defaultDispatcher + SupervisorJob())`.
   - Launch timer directly within `viewModelScope` and assign to `timerJob`.
   - Cancel `timerJob` on stop/clear.

2. **`PostsLocalDataSourceImpl.kt`**:
   - Ensure `CancellationException` is re-thrown:
     ```kotlin
     } catch (e: Exception) {
         if (e is CancellationException) throw e
         Timber.e(e, "Error accessing local database")
         PostsLocalError.DatabaseError.left()
     }
     ```

3. **`HomeViewModel.kt` & `LocationTrackerViewModel.kt`**:
   - Eliminate redundant nested `viewModelScope.launch` invocations when the caller is already operating inside a coroutine context.

### Phase 3: CI Integration & Quality Gate

1. **Validate locally:**
   ```bash
   ./gradlew lintDebug
   ```
2. **Add to `.github/workflows/android_ci.yml`:**
   ```yaml
   - name: Run Android Lint
     run: ./gradlew lintDebug
   ```

### Phase 4 (Future): Re-evaluating the Compiler Plugin

Once the project upgrades to Kotlin 2.4+ and AGP supports the corresponding toolchain:
- Evaluate `id("io.github.santimattius.structured-coroutines")` in gradual/relaxed mode.
- Annotate cross-module injected scopes (e.g. `PostsRepositoryImpl`) with `@StructuredScope`.
