# AGENTS.md - AI Coding Agent Guidelines

Multi-module Android app using Kotlin, Jetpack Compose, and MVI architecture with Arrow for functional programming.

**Tech Stack:** Kotlin 2.2.10 | Compose BOM 2026.02.01 | Koin 4.1.1 | Arrow-kt 2.2.2 | Retrofit | JUnit 5

## Build Commands

```bash
./gradlew assembleDebug                    # Debug APK
./gradlew assembleRelease                  # Release APK
./gradlew clean                            # Clean project
```

## Test Commands

```bash
./gradlew testDebugUnitTest                # Run all unit tests
./gradlew :feature:posts:testDebugUnitTest # Module tests

# Single test class
./gradlew :feature:posts:testDebugUnitTest --tests "com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModelUnitTests"

# Single test method (use wildcard for BDD names)
./gradlew :feature:posts:testDebugUnitTest --tests "*.PostsViewModelUnitTests.GIVEN get posts succeeds*"

# Coverage and screenshot tests
./gradlew koverHtmlReportDebug             # Coverage report
./gradlew verifyRoborazziDebug             # Verify screenshots
./gradlew recordRoborazziDebug             # Record new baselines
```

## Lint/Format Commands

```bash
./gradlew ktlintCheck                      # Check code style
./gradlew ktlintFormat                     # Auto-fix issues
./gradlew :app:lint                        # Android lint
```

## Code Style

- ~150 char line length (not enforced for *.kt/*.kts) | Final newline | Trailing commas
- No wildcard imports | Imports alphabetically sorted within groups
- `ktlint_function_naming_ignore_when_annotated_with = Composable`

### Import Order

```kotlin
import androidx.compose.runtime.Composable      // 1. Android (android.*, androidx.*)
import arrow.core.Either                        // 2. Third-party (arrow.*, kotlinx.*, org.koin.*)
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel  // 3. Project
```

### Naming Conventions

| Type            | Convention                                        | Example               |
|-----------------|---------------------------------------------------|-----------------------|
| ViewModel       | `{Feature}ViewModel`                              | `PostsViewModel`      |
| Repository      | `{Feature}Repository` / `{Feature}RepositoryImpl` | `PostsRepository`     |
| UseCase         | `{Action}{Entity}UseCase`                         | `AuthenticateUseCase` |
| Errors          | `{Feature}Error` (sealed interface)               | `PostsError`          |
| UI State/Events | `{Feature}UiState` / `{Feature}Event`             | `PostsUiState`        |
| Test Factory    | `{Entity}Mother`                                  | `PostMother`          |

## Architecture (MVI)

### BaseViewModel Pattern

```kotlin
internal class PostsViewModel(
    private val postsRepository: PostsRepository,
) : BaseViewModel<PostsEvent, PostsUiState>(PostsUiState.initialState) {
    override fun handleEvent(event: PostsEvent) {
        viewModelScope.launch {
            when (event) {
                PostsEvent.OnUpdatePostsRequested -> updatePosts()
            }
        }
    }
}
```

**Feature files:** `{Feature}Feature.kt` (entry point), `{Feature}Screen.kt` (stateless UI), `{Feature}Contract.kt` (events/state)

## Error Handling (Arrow Either)

```kotlin
suspend fun getPosts(): Either<PostsError, List<Post>>  // Repository signature

// Transform and handle
apiDataSource.getPosts()
    .map { posts -> posts.map { it.mapToPost() } }
    .mapLeft { error ->
        when {
            error is IOError -> PostsError.Unexpected;
            else -> PostsError.Network
        }
    }

postsRepository.getPosts().fold({ error -> /**/ }, { posts -> /**/ })  // Handle with fold
```

## State Management (Arrow Optics)

```kotlin
@optics
@Immutable
internal data class PostsUiState(
    val status: PostsStatus,
    val posts: ImmutableList<Post>,
) : UiState {
    internal companion object {
        val initialState = PostsUiState(...)
    }
}

// State updates using Arrow Optics (preferred over BaseViewModel's updateUiState methods)
_uiState.updateCopy {
    PostsUiState.status set PostsStatus.Loading
    PostsUiState.posts set posts.toImmutableList()
}
```

## Testing

```kotlin
// BDD naming
@Test
fun `GIVEN get posts succeeds WHEN initialize THEN verify get posts AND update uiState`()

// Object Mother pattern
internal object PostMother {
    operator fun invoke(id: Int = 0, title: String = "") = Post(id, title)
}

// Flow testing (Turbine)
viewModel.uiState.test {
    awaitItem() shouldBeEqualTo PostsUiState.initialState
    awaitItem() shouldBeEqualTo expectedState
}
```

## Koin DI

```kotlin
val postsModule: Module = module {
    factory<PostsRepository> { PostsRepositoryImpl(get()) }
    viewModel { PostsViewModel(get()) }
}
```

## Visibility

- ViewModels: `internal` | Use cases: `internal class` | UI components: `internal`
- Repository interfaces: `public` (cross-module API) | Repository impls: `internal`

## Project Structure

```
feature/{name}/   data/, di/, domain/, ui/contract/, ui/viewmodel/, ui/composable/
shared/           api/, core/, kotlin/, testing/
```

## References

- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Compose Lints by Slack](https://slackhq.github.io/compose-lints/rules/)
- [Ktlint Rules](https://pinterest.github.io/ktlint/latest/)
- [Google App Architecture](https://developer.android.com/topic/architecture/intro)
