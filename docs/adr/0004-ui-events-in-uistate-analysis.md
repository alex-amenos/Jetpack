# ADR 0004: UI Events, One-Off Effects, and UiState Queue Architecture Analysis

- **Status:** Proposed
- **Date:** 2026-10-03
- **Deciders:** Architecture Team
- **Target Modules:** `:feature:posts`, `:feature:movies`, `:shared:core`

---

## 1. Executive Summary

This Architectural Decision Record (ADR) presents an in-depth architectural review of `:feature:posts` and `:feature:movies`, comparing their current state and event handling implementations with the paradigm outlined in the industry article [**"UI Events in UiState: Start Simple, Add a Queue Only When You Need One"**](https://proandroiddev.com/ui-events-in-uistate-start-simple-add-a-queue-only-when-you-need-one-5b59dac0261e).

### Core Question
> *Is the current codebase implementation better, or is the article's "Start Simple, Add a Queue Only When You Need One" approach superior? What are the respective PROS and CONS, and what constitutes best approach and best practice for modern Android development with Jetpack Compose?*

### Definitive Verdict
The article's paradigm provides a **fundamentally more robust, deterministic, and scalable architectural model** than the current implementation. However, the current codebase already partially applies the "Start Simple" pattern (in `:feature:posts`), but does so with critical architectural flaws:
1. **Navigation Contract Pollution & Runtime Crash Hazard:** ViewModel event interfaces (`PostsEvent`, `MoviesEvent`) declare navigation actions (`GoBackRequested`, `OnPostClicked`, `MovieClicked`), yet their ViewModels actively throw `IllegalStateException` if they receive them.
2. **Unkeyed Nullable Transient State:** Nullable error fields in `PostsUiState` suffer from silent event overwriting (event dropping) during rapid failure bursts, and Compose `LaunchedEffect` triggers are bound to string/error equality rather than unique event instances.
3. **Inconsistent State Machine Transitions:** Dismissing an error naively forces screen status back to `PostsStatus.Success` even when data fetching completely failed.
4. **Conflation of Persistent State vs. Transient One-Off Events:** A full-screen error (as seen in `:feature:movies` `MovieDetailScreen`) is persistent state that requires no queue or dismissal, whereas snackbars/toasts are transient effects that require acknowledgment or queueing.

Adopting the article's graduated approach—**starting with an explicitly identified, consumable nullable event in `UiState`, and transitioning to an immutable FIFO event queue only when concurrent or burst events must not be lost**—alongside cleanly decoupling navigation from ViewModel contracts represents the optimal best practice.

---

## 2. Context & Problem Statement

### 2.1 The Challenge of One-Off Events in Declarative UI
In modern Android architecture (MVI, Unidirectional Data Flow), the UI is modeled as a pure representation of state:

$$\text{UI} = f(\text{UiState})$$

While persistent state (content lists, loading indicators, form input) maps naturally to `StateFlow<UiState>`, handling **transient one-off effects** (e.g., displaying a Snackbar, showing a one-time Toast, triggering haptic feedback, or navigating) has been one of the most debated topics in modern Android:

1. **The Anti-Pattern of Channels / SharedFlow Side-Effect Buses:**
   - For years, teams relied on `Channel<UiEffect>(Channel.BUFFERED)` or `MutableSharedFlow<UiEffect>()` alongside `StateFlow<UiState>`.
   - **Flaws:**
     - **Lifecycle Desynchronization:** If the UI is in the background (`Lifecycle.State.STOPPED`), collecting flows can either drop events (unbuffered `SharedFlow`) or buffer events that fire abruptly upon resumption.
     - **Lost State Reproducibility:** The UI state is no longer a single source of truth. Testing or reproducing a screen solely from `UiState` becomes impossible because one-off effects bypass the state stream.
     - **Multi-Stream Synchronization:** Composables must manage multiple collection coroutines, increasing recomposition and synchronization bugs.

2. **Google's Stance (Android Architecture Guidelines):**
   - Google recommends that ViewModel-to-UI events should be modeled **directly inside `UiState`**.
   - The UI observes the event within `UiState`, renders the transient visual effect, and dispatches a consumption event back to the ViewModel to reset or dequeue the event.

### 2.2 The Article's Thesis: "Start Simple, Add a Queue Only When You Need One"
The ProAndroidDev article formulates a pragmatic, two-tiered progression:

```
┌────────────────────────────────────────────────────────┐
│ Phase 1: Start Simple (Single Nullable Event)          │
│ - Model transient action as nullable field in UiState  │
│ - UI renders effect and calls ViewModel consumption     │
│ - Zero queue boilerplate; optimal for 90% of screens   │
└──────────────────────────┬─────────────────────────────┘
                           │ When burst/concurrent events
                           │ or FIFO guarantees required
                           ▼
┌────────────────────────────────────────────────────────┐
│ Phase 2: Add a Queue Only When You Need One             │
│ - Model queue as ImmutableList<UiEventItem> in UiState │
│ - Each event carries a unique ID (UUID / Long)         │
│ - UI renders head of queue, acknowledges completion     │
│ - ViewModel dequeues by ID; zero dropped events        │
└────────────────────────────────────────────────────────┘
```

---

## 3. Deep Architectural Audit of Current Codebase

### 3.1 Module: `:feature:posts`

#### Current Contracts & Implementation
In `PostsContract.kt`:
```kotlin
internal sealed interface PostsEvent : UiEvent {
    data object OnUpdatePostsRequested : PostsEvent
    data object GoBackRequested : PostsEvent
    data object DismissErrorRequested : PostsEvent
    data class OnPostClicked(val post: Post) : PostsEvent
}

@optics
@Immutable
internal data class PostsUiState(
    val status: PostsStatus,
    val posts: ImmutableList<Post>,
    val error: PostUiError?,
) : UiState {
    val isLoading: Boolean = status == PostsStatus.Loading
    // ...
}
```

In `PostsViewModel.kt`:
```kotlin
override fun handleEvent(event: PostsEvent) {
    viewModelScope.launch {
        when (event) {
            PostsEvent.GoBackRequested -> throw IllegalStateException("Go back not implemented in ViewModel")
            PostsEvent.OnUpdatePostsRequested -> updatePosts()
            is PostsEvent.OnPostClicked -> throw IllegalStateException("On post clicked not implemented in ViewModel")
            is PostsEvent.DismissErrorRequested -> dismissError()
        }
    }
}

private fun dismissError() {
    updateUiState {
        copy {
            PostsUiState.status set PostsStatus.Success
            PostsUiState.error set null
        }
    }
}
```

In `PostsFeature.kt` & `PostScreen.kt`:
```kotlin
// PostsFeature.kt: UI layer intercepts navigation events
PostsScreen(
    state = viewModel.uiState.collectAsStateWithLifecycle().value,
    handleEvent = { event ->
        when (event) {
            PostsEvent.GoBackRequested -> onBack()
            is PostsEvent.OnPostClicked -> onPostSelected(event.post.id)
            else -> event.handleEvent()
        }
    },
)

// PostScreen.kt: Snackbar rendering
@Composable
private fun PostSnackbarError(
    errorMessage: String,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
) {
    LaunchedEffect(errorMessage) {
        val result = snackbarHostState.showSnackbar(message = errorMessage)
        when (result) {
            SnackbarResult.Dismissed, SnackbarResult.ActionPerformed -> onDismiss()
        }
    }
}
```

#### Detailed Findings & Anti-Patterns in `:feature:posts`

1. **Navigation Contract Pollution & Runtime Crash Hazard:**
   - `PostsEvent` bundles both business intents (`OnUpdatePostsRequested`, `DismissErrorRequested`) and navigation intents (`GoBackRequested`, `OnPostClicked`).
   - The ViewModel explicitly throws `IllegalStateException` if it receives navigation events.
   - This relies on an implicit, fragile convention where `PostsFeature` must manually intercept navigation events before calling `handleEvent()`.
   - If `handleEvent` is wired directly in previews, component refactors, or automated tests, the application crashes immediately at runtime.
   - **Architectural Violation:** Navigation in Jetpack Compose is a UI-level routing concern, not a ViewModel-managed domain action.

2. **Unkeyed Nullable State & Event Dropping:**
   - `PostsUiState.error` holds a single `PostUiError?`.
   - If an initial network error occurs, and while the snackbar is displaying a secondary failure occurs (e.g. background sync or user-triggered retry), the second error silently overwrites the first.
   - Because `PostSnackbarError` uses `LaunchedEffect(errorMessage)` keyed on the string content:
     - If the identical error recurs (e.g., repeating `NoConnectivity`), the key does not change, and `LaunchedEffect` **fails to re-trigger**.

3. **Inconsistent State Machine Transition:**
   - In `dismissError()`, the ViewModel executes:
     ```kotlin
     PostsUiState.status set PostsStatus.Success
     PostsUiState.error set null
     ```
   - If initial loading failed and `posts` is empty, dismissing the error forces `status` to `PostsStatus.Success`. The UI now reports a "Success" state with empty data rather than retaining an idle/empty or persistent failure state.

---

### 3.2 Module: `:feature:movies`

#### Current Contracts & Implementation
In `MoviesContract.kt` & `MoviesViewModel.kt`:
```kotlin
@Parcelize
@Immutable
data class MoviesState(
    val searchQuery: String,
) : UiState, Parcelable

sealed interface MoviesEvent : UiEvent {
    data class SearchQueryChanged(val query: String) : MoviesEvent
    data class MovieClicked(val movieId: Int) : MoviesEvent
    data object GoBackRequested : MoviesEvent
}
```

In `MoviesViewModel.kt`:
```kotlin
val moviesPagingFlow: Flow<PagingData<Movie>> =
    searchQueryFlow
        .debounce(SEARCH_DEBOUNCE_DELAY)
        .flatMapLatest { query -> movieRepository.searchMovies(query) }
        .cachedIn(viewModelScope)

override fun handleEvent(event: MoviesEvent) {
    when (event) {
        is MoviesEvent.SearchQueryChanged -> { /* update state & query */ }
        is MoviesEvent.MovieClicked -> throw IllegalStateException("MovieClicked event should be handled in the UI layer, not in the ViewModel.")
        MoviesEvent.GoBackRequested -> throw IllegalStateException("Go back not implemented in ViewModel")
    }
}
```

In `MovieDetailContract.kt` & `MovieDetailScreen.kt`:
```kotlin
@Parcelize
@Immutable
data class MovieDetailState(
    val isLoading: Boolean,
    val movieId: Int?,
    val movie: MovieDetails?,
    val error: MovieError?,
) : UiState, Parcelable

// MovieDetailScreen.kt: Fullscreen error presentation
when {
    state.isLoading -> CircularProgressIndicator(...)
    state.error != null -> MovieErrorContent(errorMessage = state.error.toMessage(), ...)
    state.movie != null -> MovieSuccessContent(...)
}
```

In `MoviesViewModelTest.kt`:
```kotlin
@Test
fun `GIVEN movie id WHEN dispatched THEN throws IllegalStateException`() {
    assertThrows(IllegalStateException::class.java) {
        viewModel.handleEvent(MoviesEvent.MovieClicked(123))
    }
}

@Test
fun `GIVEN click WHEN dispatched THEN throws IllegalStateException`() {
    assertThrows(IllegalStateException::class.java) {
        viewModel.handleEvent(MoviesEvent.GoBackRequested)
    }
}
```

#### Detailed Findings & Architectural Distinctions in `:feature:movies`

1. **Exemplary Delegation to Paging 3:**
   - `MoviesViewModel` does not store movie lists or pagination errors in `MoviesState`.
   - Instead, pagination state and error handling are managed via Paging 3's `moviesPagingFlow` and `LazyPagingItems.loadState`.
   - In `MoviesScreen.kt`, the snackbar error displays directly based on `loadState.refresh` or `loadState.append`, triggering `movies.retry()`.
   - **Architectural Lesson:** Specialized libraries with built-in reactive state streams should not be needlessly duplicated inside `UiState`.

2. **Persistent UI State vs. Transient One-Off Events:**
   - In `MovieDetailScreen`, when `state.error != null`, the screen displays `MovieErrorContent` as a full-screen replacement for the content.
   - **Crucial Distinction:** This error is **persistent state**, not a transient event. The error must remain visible until the user navigates away or retries.
   - Attempting to force an event queue or auto-dismiss mechanism onto full-screen errors is an architectural anti-pattern; persistent states belong directly in `UiState` without consumption logic.

3. **Confirmed Contract Anti-Pattern via Unit Tests:**
   - As evidenced in `MoviesViewModelTest.kt`, the test suite explicitly verifies that `MovieClicked` and `GoBackRequested` throw `IllegalStateException`.
   - The test suite is forced to test that the ViewModel crashes when given events that are declared in its own public contract.

---

## 4. Comprehensive Comparison: Current Code vs. Article Approach

| Architectural Dimension | Current Codebase Implementation | Article: "Start Simple" (Nullable Event) | Article: "Add a Queue When Needed" |
| :--- | :--- | :--- | :--- |
| **Single Source of Truth** | **Partial:** `UiState` holds errors, but navigation is fragmented between UI and crashing ViewModels. | **High:** All visual states and transient prompts are serialized in `UiState`. | **Complete:** Full history of pending transient actions lives in `UiState`. |
| **Event Loss & Concurrency** | **High Risk:** Rapid consecutive errors overwrite each other unobserved. | **Medium Risk:** Overwrites possible if second event occurs before UI consumes first. | **Zero Risk:** Strict FIFO order; all events retained until individually consumed. |
| **Recomposition & Screen Rotation** | **Fragile:** `LaunchedEffect(errorMessage)` misses identical repeating errors. | **Robust (with ID):** Keyed on unique ID (`UUID`/`Long`), replaying cleanly on recreation. | **Robust:** Queue survives rotation; current head continues presentation. |
| **State Machine Consistency** | **Poor:** Resetting error forces `PostsStatus.Success` regardless of underlying data. | **Configurable:** Consuming message does not perturb foundational screen state. | **Configurable:** Dequeueing leaves primary content/status uncorrupted. |
| **Separation of Concerns** | **Violated:** Navigation events defined in ViewModel contract but crash if dispatched. | **Clear:** Navigation kept outside ViewModel; events strictly express UI feedback. | **Clear:** Granular separation between UI routing and state-driven notifications. |
| **Boilerplate & Complexity** | **Low-Medium:** Ad-hoc callbacks, manual error mapping, interceptors in `Feature`. | **Minimal:** 1 data class wrapper, 1 consumption event, standard Compose integration. | **Moderate:** Queue data structure, enqueue/dequeue methods, ID management. |
| **Testability (Turbine / Unit)** | **Awkward:** Must assert exceptions for navigation; state transitions can be inconsistent. | **Clean:** Simple assertion on `awaitItem().userMessage shouldBeEqualTo expected`. | **Exhaustive:** Can assert exact queue sequence, processing order, and drainage. |

---

## 5. PROS and CONS Evaluation

### 5.1 Current Codebase Implementation
#### PROS
- Minimal extra classes: directly uses `PostUiError` and `MovieError` enums/sealed classes.
- Familiar MVI layout with Arrow Optics (`updateCopy`, lenses).
- `feature:movies` wisely avoids duplicating pagination error state by leveraging Paging 3's built-in `LoadState`.

#### CONS
- **Fatal Navigation Anti-Pattern:** ViewModel contracts (`PostsEvent`, `MoviesEvent`) contain events that crash the ViewModel with `IllegalStateException`.
- **Silent Event Loss:** In `feature:posts`, consecutive errors overwrite pending errors before the user can acknowledge them.
- **Compose Key Invalidation Bugs:** Keying `LaunchedEffect` on `errorMessage: String` prevents re-triggering when identical consecutive errors occur.
- **Corrupted Status Machine:** `dismissError()` sets `PostsStatus.Success` even when data loading failed.
- **Lack of Process Death Hardening:** `PostsUiState` does not implement `Parcelable` or persist through `SavedStateHandle`.

---

### 5.2 Article Approach: "Start Simple" (Single Nullable Event in UiState)
```kotlin
data class UiMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
)

data class PostsUiState(
    val status: PostsStatus,
    val posts: ImmutableList<Post>,
    val userMessage: UiMessage? = null,
)
```

#### PROS
- **Simplicity:** Very low mental overhead; no queue management needed.
- **Solves the Recomposition Bug:** Keying `LaunchedEffect(uiState.userMessage?.id)` guarantees exact-once execution even if the error message string is identical.
- **100% Declarative & Lifecycle Safe:** When UI is in background (`STOPPED`), state remains preserved; when resumed, the message is displayed.
- **Perfect Fit for 90% of Screens:** Most mobile screens only ever have at most one transient message at a time.

#### CONS
- If two asynchronous operations fail in immediate succession, the second overwrites the first before dismissal, dropping the first message.
- Requires explicit round-trip acknowledgment (`UserMessageDismissed(id)`) from the UI.

---

### 5.3 Article Approach: "Add a Queue Only When You Need One" (Immutable Queue in UiState)
```kotlin
data class PostsUiState(
    val status: PostsStatus,
    val posts: ImmutableList<Post>,
    val messageQueue: ImmutableList<UiMessage> = persistentListOf(),
)
```

#### PROS
- **Guaranteed Delivery (Zero Event Dropping):** Every single error or notification is preserved in FIFO order.
- **Idempotent Acknowledgment:** Consuming by `id` ensures that even with out-of-order execution, only the intended event is removed from the queue.
- **Pure Functional State:** State transitions can be tested deterministically with Turbine down to each enqueue and dequeue step.
- **Configuration Change Resilient:** If screen rotates mid-snackbar, the head of the queue remains at the head and resumes correctly.

#### CONS
- **Boilerplate:** Requires managing list operations, deduplication, and ID generation.
- **Risk of Stale Queues:** If UI fails to acknowledge an event (e.g. composable unmounts without calling dismiss), events can accumulate in memory unless bounded or cleared on navigation.
- **Overkill for Static Screens:** Unnecessary complexity for simple screens that never encounter burst events.

---

## 6. Best Approach and Best Practices for Jetpack

Based on the empirical analysis of `:feature:posts` and `:feature:movies`, the recommended architecture combines the strengths of the article's graduated pattern with modern Jetpack Compose idioms.

### 6.1 The Four-Tier Event Taxonomy
To eliminate contract pollution and runtime exceptions, all events in the application must be categorized into one of four distinct tiers:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        EVENT CLASSIFICATION                            │
├────────────────────────┬─────────────────────┬─────────────────────────┤
│ Category               │ Direction           │ Mechanism               │
├────────────────────────┼─────────────────────┼─────────────────────────┤
│ 1. User Intent         │ UI -> ViewModel     │ ViewModel.handleEvent() │
│ 2. Navigation Action   │ UI -> NavController │ Direct Compose Callbacks│
│ 3. Persistent UI State │ ViewModel -> UI     │ UiState data class      │
│ 4. Transient UI Effect │ ViewModel -> UI     │ UiState (Nullable/Queue)│
└────────────────────────┴─────────────────────┴─────────────────────────┘
```

#### Category 1: User Intents (UI $\rightarrow$ ViewModel)
- Actions that invoke business logic, repository calls, or state mutations.
- *Examples:* `OnUpdatePostsRequested`, `SearchQueryChanged(query)`.
- *Rule:* Handled exclusively via `BaseViewModel.handleEvent(event)`.

#### Category 2: Navigation Actions (UI $\rightarrow$ Compose Navigation)
- Actions that switch screens or navigate backwards.
- *Examples:* Back button clicks, selecting an item in a list to view details.
- *Rule:* **Never include navigation actions in ViewModel `UiEvent` sealed hierarchies.** ViewModel should not know about UI navigation graphs. Pass direct lambdas from the feature composable down to the presentation composable:
  ```kotlin
  @Composable
  fun PostsFeature(
      onPostSelected: (Long) -> Unit,
      onBack: () -> Unit,
  ) {
      val viewModel = koinViewModel<PostsViewModel>()
      PostsScreen(
          state = viewModel.uiState.collectAsStateWithLifecycle().value,
          onPostClicked = onPostSelected,
          onBackClicked = onBack,
          onRefresh = { viewModel.handleEvent(PostsEvent.Refresh) },
          onDismissMessage = { id -> viewModel.handleEvent(PostsEvent.DismissMessage(id)) },
      )
  }
  ```

#### Category 3: Persistent UI State (ViewModel $\rightarrow$ UI)
- Screen representations that remain on screen until a new state transition occurs.
- *Examples:* `isLoading`, list of posts, search query, **and full-screen error states** (e.g., `MovieDetailState.error`).
- *Rule:* Modeled directly as properties or sealed state hierarchies in `UiState`. Do **not** apply dismissal or queueing logic to persistent states.

#### Category 4: Transient UI Effects (ViewModel $\rightarrow$ UI)
- Ephemeral visual feedback (Snackbars, Toasts, transient banners).
- *Rule:* Apply the article's graduated rule: **Start Simple with an identified nullable event; add a queue only when concurrent or burst events must not be lost.**

---

### 6.2 Implementation Blueprint: "Start Simple" with Unique Identification

For `:feature:posts` and similar feature modules, replace the unkeyed nullable error with an identified `UiMessage`:

#### 1. Contract Definition
```kotlin
package com.alxnophis.jetpack.posts.ui.contract

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.posts.data.model.Post
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class UiMessage(
    val id: Long = System.currentTimeMillis(),
    @StringRes val messageRes: Int,
)

internal sealed interface PostsEvent : UiEvent {
    data object RefreshRequested : PostsEvent
    data class DismissMessageRequested(val messageId: Long) : PostsEvent
}

@optics
@Immutable
internal data class PostsUiState(
    val status: PostsStatus,
    val posts: ImmutableList<Post>,
    val userMessage: UiMessage? = null,
) : UiState {
    val isLoading: Boolean = status == PostsStatus.Loading
}
```

#### 2. ViewModel Implementation
```kotlin
internal class PostsViewModel(
    private val postsRepository: PostsRepository,
    initialUiState: PostsUiState = PostsUiState.initialState,
) : BaseViewModel<PostsEvent, PostsUiState>(initialUiState) {

    override fun handleEvent(event: PostsEvent) {
        viewModelScope.launch {
            when (event) {
                PostsEvent.RefreshRequested -> updatePosts()
                is PostsEvent.DismissMessageRequested -> dismissMessage(event.messageId)
            }
        }
    }

    private fun dismissMessage(messageId: Long) {
        updateUiState {
            if (userMessage?.id == messageId) {
                copy {
                    PostsUiState.userMessage set null
                }
            } else {
                this
            }
        }
    }
}
```

#### 3. Compose Screen Consumption
```kotlin
@Composable
internal fun PostsScreen(
    state: PostsUiState,
    onPostClicked: (Long) -> Unit,
    onBackClicked: () -> Unit,
    onRefresh: () -> Unit,
    onDismissMessage: (Long) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    state.userMessage?.let { message ->
        val text = stringResource(id = message.messageRes)
        LaunchedEffect(message.id) {
            try {
                snackbarHostState.showSnackbar(message = text)
            } finally {
                onDismissMessage(message.id)
            }
        }
    }

    Scaffold(
        topBar = { CoreTopBar(title = stringResource(R.string.posts_title), onBack = onBackClicked) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        // Render content...
    }
}
```

---

### 6.3 When and How to Add an Event Queue

If a module involves concurrent operations (e.g., `:feature:file-downloader` downloading multiple files simultaneously and reporting individual failures, or batch deletions with undo options), evolve `userMessage: UiMessage?` into `messageQueue: ImmutableList<UiMessage>`:

```kotlin
@optics
@Immutable
internal data class BatchDownloadUiState(
    val files: ImmutableList<DownloadItem>,
    val messageQueue: ImmutableList<UiMessage> = persistentListOf(),
) : UiState

// ViewModel operations:
private fun enqueueMessage(message: UiMessage) {
    _uiState.updateCopy {
        BatchDownloadUiState.messageQueue transform { it.add(message) }
    }
}

private fun dismissMessage(messageId: Long) {
    _uiState.updateCopy {
        BatchDownloadUiState.messageQueue transform { queue ->
            queue.removeAll { it.id == messageId }
        }
    }
}

// In Compose:
val currentMessage = state.messageQueue.firstOrNull()
LaunchedEffect(currentMessage?.id) {
    if (currentMessage != null) {
        try {
            snackbarHostState.showSnackbar(message = context.getString(currentMessage.messageRes))
        } finally {
            onDismissMessage(currentMessage.id)
        }
    }
}
```

---

## 7. Migration Plan for the Codebase

1. **Step 1: Cleanse ViewModel Event Contracts (`:feature:posts`, `:feature:movies`)**
   - Remove `GoBackRequested`, `OnPostClicked`, and `MovieClicked` from `PostsEvent` and `MoviesEvent`.
   - Update `PostsFeature` and `MoviesFeature` to supply navigation callbacks directly into screens.
   - Delete the exception-throwing branches from `PostsViewModel` and `MoviesViewModel`.
   - Remove unit tests expecting `IllegalStateException` on navigation dispatches.

2. **Step 2: Upgrade Transient Errors with Identified `UiMessage` in `:feature:posts`**
   - Introduce `UiMessage(val id: Long, @StringRes val messageRes: Int)` in `PostContract.kt`.
   - Transition `error: PostUiError?` to `userMessage: UiMessage?`.
   - Key `LaunchedEffect` on `message.id` in `PostSnackbarError`.
   - Fix `dismissError` so it clears `userMessage` without corrupting `PostsStatus`.

3. **Step 3: Document Architectural Guidelines in `AGENTS.md`**
   - Add the 4-tier event taxonomy to `AGENTS.md` to prevent recurring navigation contract pollution across new feature modules.

---

## 8. Summary & Conclusion

| Question | Answer |
| :--- | :--- |
| **Is the current codebase better than the article?** | **No.** The current codebase suffers from navigation contract pollution (throwing exceptions in ViewModels), unkeyed event dropping during bursts, and state machine corruption on dismiss. |
| **Is the article's approach superior?** | **Yes.** The article's graduated model ("Start Simple, Add a Queue Only When You Need One") provides exact-once delivery, lifecycle safety, and clean separation between persistent state and transient effects. |
| **What is the best practice?** | 1. Never put navigation in ViewModel event contracts.<br>2. Differentiate between persistent state (e.g. fullscreen errors, Paging 3) and transient effects (e.g. snackbars).<br>3. Model transient effects in `UiState` using unique IDs (`UiMessage(id)`).<br>4. Start with a nullable field; upgrade to an immutable queue only for concurrent/burst operations. |
