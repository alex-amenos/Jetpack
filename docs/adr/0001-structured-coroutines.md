# ADR 0001: Structured Coroutines Lint Integration

- **Status:** Accepted
- **Date:** 2026-09-13
- **Deciders:** Architecture Team

---

## Context

Kotlin coroutines require careful adherence to structured concurrency principles to prevent memory leaks, unmanaged background work, silent cancellation breaks, and main-thread blocking operations. 

During an audit of the codebase, several recurring anti-patterns were observed:
1. **Unmanaged Coroutine Scopes in ViewModels:** Instances where custom scopes (`CoroutineScope(dispatcher + SupervisorJob())`) bypassed `viewModelScope` and were not cancelled on `onCleared()`.
2. **Swallowing `CancellationException`:** Catch-all blocks (`catch (e: Exception)`) inside suspend database operations without re-throwing `CancellationException`, preventing proper cancellation propagation.
3. **Redundant Coroutine Launches:** Calling `viewModelScope.launch` from within blocks or event handlers that were already running in a coroutine context or only updating synchronous state.

The [`santimattius/structured-coroutines`](https://github.com/santimattius/structured-coroutines) toolkit was evaluated as a static analysis solution. The toolkit provides multiple artifacts:
- **Android Lint Rules (`structured-coroutines-lint-rules`)**
- **Annotations (`structured-coroutines-annotations`)**
- **Kotlin Compiler Plugin (`structured-coroutines-compiler`)**
- **Detekt Rules (`structured-coroutines-detekt-rules`)**

---

## Decision

1. **Adopt Android Lint Rules Only:**
   - Integrate `io.github.santimattius:structured-coroutines-lint-rules` as a `lintChecks` dependency in `buildSystem/gradle/common-android-base.gradle` and `buildSystem/gradle/common-app-base.gradle`.
   - Android Lint executes on standard Android UAST tooling, carrying **zero compiler ABI break risk** and seamlessly fitting the repository's existing lint pipeline.

2. **Configure Rule Severities in `lint.xml`:**
   - **Errors:** `GlobalScopeUsage`, `InlineCoroutineScope`, `ViewModelScopeLeak`, `MainDispatcherMisuse`, `RunBlockingInSuspend`, `JobInBuilderContext`, `CancellationExceptionSubclass`, `AsyncWithoutAwait`, `LifecycleAwareScope`.
   - **Warnings:** `CancellationExceptionSwallowed`, `LifecycleAwareFlowCollection`, `RedundantLaunchInCoroutineScope`, `SuspendInFinally`, `ChannelNotClosed`, `ScopeReuseAfterCancel`, etc.
   - **Ignored:** Heuristic checks with high false-positive rates (`LoopWithoutYield`, `UnstructuredLaunch`).

3. **Defer Compiler Plugin & Detekt Rules:**
   - **Compiler Plugin:** Targets Kotlin 2.3.x/2.4.x K2 compiler internals, whereas the project is on Kotlin 2.2.x. K2 compiler plugins are tightly coupled to compiler ABI and introduce build fragility across version updates. This will be re-evaluated when the project upgrades to Kotlin 2.4+.
   - **Detekt Rules:** Skipped because the project standardizes on `ktlint`.

4. **Handling Legitimate Global / Application Scopes:**
   - For legitimate application-lifetime scopes created for dependency injection (e.g. `single(named("applicationScope"))` in `CoreModule.kt`), use `@Suppress("InlineCoroutineScope")`.

---

## Consequences

### Positive
- **Automated Quality Gates:** Coroutine anti-patterns are surfaced directly in Android Studio and enforced in CI via `./gradlew lintDebug`.
- **Zero Runtime Overhead:** No additional runtime bytecode or libraries are bundled in release artifacts.
- **Main Safety Enforced:** Domain use cases and repositories manage their own dispatchers via `flowOn` or `withContext`, keeping ViewModel invocations on `Dispatchers.Main.immediate`.
- **Proper Cancellation Propagation:** Re-throwing `CancellationException` ensures coroutine hierarchies cancel cleanly without orphaned work.

### Negative / Trade-offs
- Android Lint adds build time overhead during full CI runs (mitigated by Gradle build cache and scoped module tasks).
- Legitimate scope instantiation for DI singletons requires deliberate `@Suppress("InlineCoroutineScope")` annotations.
