# ADR 0003: Reactive Theming, Connectivity Monitoring, and ViewModel Patterns

- **Status:** Accepted
- **Date:** 2026-09-30
- **Deciders:** Architecture Team

---

## Context

As part of Phase 1 architectural improvements across the application shell (`:feature:root`) and shared foundation (`:shared:core`), two cross-cutting capabilities were required:
1. **Dynamic theme propagation:** User-selected theme options (System, Light, Dark) persisted in `:feature:settings` needed to be observed application-wide and applied to the root Compose tree without requiring Activity re-creation.
2. **Reactive connectivity monitoring:** An application-wide mechanism to observe network availability and display visual feedback (`OfflineIndicator`) when connectivity is lost.

Additionally, guidelines were needed to clarify when to use `BaseViewModel` versus standard Android `ViewModel` with `stateIn`.

---

## Decision

1. **ViewModel Architecture by Use Case:**
   - **`BaseViewModel<Event, State>`:** Reserved for feature modules with complex unidirectional UI event flows, multi-action user interactions, and Arrow Optics state manipulation (e.g., `:feature:posts`, `:feature:authentication`).
   - **Standard `ViewModel` with `stateIn`:** Used for simpler ViewModels or composition/orchestration ViewModels (such as `SettingsViewModel` and `RootViewModel`) that combine repository flows into a single reactive `StateFlow` via `stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = ...)`.

2. **Global Theme Propagation:**
   - Expose `SettingsRepository` and `ThemeOptions` from `:feature:settings`.
   - Introduce `LocalDarkTheme` composition local in `:shared:core`'s `AppTheme` to allow dynamic Compose theme propagation without Activity restarts.
   - `RootViewModel` observes `settingsRepository.getSettingsFlow()` and combines its theme preference with connectivity state for `RootActivity`.

3. **Reactive Connectivity Monitoring:**
   - Define `NetworkMonitor` interface and `ConnectivityNetworkMonitor` implementation in `:shared:core` using `ConnectivityManager.NetworkCallback` with `callbackFlow`.
   - Validate both `NET_CAPABILITY_INTERNET` and `NET_CAPABILITY_VALIDATED` capabilities before considering a network active.
   - Declare `android.permission.ACCESS_NETWORK_STATE` in `:shared:core`'s AndroidManifest.
   - Introduce animated `OfflineIndicator` composable in `:feature:root` with edge-to-edge window insets handling and status bar appearance coordination.
