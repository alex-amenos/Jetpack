# AGENTS.md - AI Coding Agent Guidelines

Multi-module Android app using Kotlin, Jetpack Compose, and MVI architecture with Arrow for functional programming.

**Tech Stack:** Kotlin 2.1.10 | Compose BOM 2025.11.01 | Koin DI | Arrow-kt | Retrofit | JUnit 5

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

# Single test method
./gradlew :feature:posts:testDebugUnitTest --tests "*.PostsViewModelUnitTests.GIVEN get posts succeeds*"

# Screenshot tests (Roborazzi)
./gradlew verifyRoborazziDebug             # Verify
./gradlew recordRoborazziDebug             # Record baselines
```

## Lint/Format Commands

```bash
./gradlew ktlintCheck                      # Check code style
./gradlew ktlintFormat                     # Auto-fix issues
./gradlew :app:lint                        # Android lint
```

## Code Style

- Max line length: aim for ~150 chars for readability (not enforced for *.kt/*.kts in .editorconfig) | Final newline required | Trailing commas encouraged
- No wildcard imports | Imports alphabetically sorted within groups
- Composable functions exempt from function naming rules

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
| Errors          | `{Feature}Error` (sealed)                         | `PostsError`          |
| UI State/Events | `{Feature}UiState` / `{Feature}Event`             | `PostsUiState`        |
| Test Factory    | `{Entity}Mother`                                  | `PostMother`          |

## Architecture

### MVI with BaseViewModel

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

### Feature/Screen Separation

- `{Feature}Feature.kt` - Entry point, connects ViewModel with Screen
- `{Feature}Screen.kt` - Stateless composable UI
- `{Feature}Contract.kt` - Events, UiState, sealed classes

## Error Handling (Arrow Either)

```kotlin
// Repository returns Either
suspend fun getPosts(): Either<PostsError, List<Post>>

// Transform errors with mapLeft
apiDataSource.getPosts()
    .map { posts -> posts.map { it.mapToPost() } }
    .mapLeft { error: CallError ->
        when {
            error is IOError -> PostsError.Unexpected
            error is HttpError && error.code >= 500 -> PostsError.Server
            else -> PostsError.Network
        }
    }

// Handle with fold
postsRepository.getPosts().fold(
    { error -> /* handle error */ },
    { posts -> /* handle success */ },
)

// Sealed class for errors
sealed class PostsError {
    data object Network : PostsError()
    data object Server : PostsError()
}
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

// Updates
_uiState.updateCopy { PostsUiState.status set PostsStatus.Loading }
```

## Testing

### BDD Test Naming

```kotlin
@Test
fun `GIVEN get posts succeeds WHEN initialize THEN verify get posts AND update uiState`()
```

### Object Mother Pattern

```kotlin
internal object PostMother {
    operator fun invoke(id: Int = 0, title: String = "") = Post(id, title)
}
```

### Flow Testing (Turbine)

```kotlin
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

- ViewModels: `internal` | Use cases: `internal class`
- Repository interfaces: `public` (cross-module API) | Repository impls: `internal` when bound via DI in the same module, `public` only if needed across modules | UI components: `internal`

## Project Structure

```
feature/{name}/
    data/           # Repository implementations, API models
    di/             # Koin modules
    domain/         # Use cases, domain models, errors
    ui/contract/    # Events, UiState
    ui/viewmodel/   # ViewModels
    ui/composable/  # Compose screens
shared/
    api/            # REST API clients
    core/           # Base classes, core UI
    kotlin/         # Utilities
    testing/        # Test utilities
```

## References

- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Compose Lints by Slack](https://slackhq.github.io/compose-lints/rules/)
- [Ktlint Rules](https://pinterest.github.io/ktlint/latest/)
- [Google App Architecture](https://developer.android.com/topic/architecture/intro)
