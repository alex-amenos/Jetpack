# Movie Search Application Plan

This document outlines the plan to implement a simple movie search application using TheMovieDb API, adhering to the project's MVI architecture and conventions.

## 1. Configuration & Dependencies
* **Paging 3 Setup**: Add `androidx.paging:paging-runtime` and `androidx.paging:paging-compose` to `gradle/libs.versions.toml`. Add the compose dependency to `:feature:movies`.
* **API Key Injection**: Configure `:shared:api`'s `build.gradle` to read `THEMOVIEDB_ORG_API_KEY` from `local.properties` securely, making it available as a `BuildConfig` variable.

## 2. Network Layer (`:shared:api` module)
* **Retrofit Client**: Create `TheMovieDbRetrofitFactory` (patterned after `JsonPlaceholderRetrofitFactory`). It will include an OkHttp Interceptor to automatically append the `Authorization: Bearer <API_KEY>` and `accept: application/json` headers to all TMDB requests.
* **Services**: Create `TheMovieDbServices.kt` with endpoints:
  * `@GET("search/movie")` for paginated search.
  * `@GET("movie/{movie_id}")` for details.
* **Models**: Create standard Kotlinx Serialization API models (`MovieSearchResponseApiModel`, `MovieApiModel`, `MovieDetailsApiModel`).
* **DI**: Wire these up in a new Koin module `TheMovieDbModule.kt`.

## 3. Data & Domain Layers (`:feature:movies` module)
* **Domain Models**: Define `Movie` and `MovieDetails` data classes.
* **Paging Source**: Create `MoviePagingSource` extending Paging 3's `PagingSource<Int, Movie>`. It will execute API calls using the search query and handle pagination keys.
* **Repository**: Create `MovieRepositoryImpl` offering:
  * `fun searchMovies(query: String): Flow<PagingData<Movie>>`
  * `suspend fun getMovieDetails(id: Int): Either<CallError, MovieDetails>`

## 4. Presentation Layer (`:feature:movies` module)
* **MVI State & Events**: 
  * Define `MoviesState` (holding the query string) and `MoviesEvent` (QueryChanged, MovieClicked). 
  * Define `MovieDetailState` and `MovieDetailEvent`.
* **MoviesScreen**:
  * **ViewModel**: Monitor the `searchQuery` state. We will use a Coroutines `snapshotFlow` with `.debounce(500L)` so that typing automatically triggers a search without needing a search button.
  * **UI**: A Compose `TextField` for search input. A `LazyColumn` consuming the Pager via `collectAsLazyPagingItems()` to render the list of movies and handle scroll-based pagination.
* **MovieDetailScreen**:
  * **ViewModel**: Fetches and stores the details for a specific `movieId`.
  * **UI**: Shows the movie's title, overview, poster image (using Coil), release date, and rating.

## 5. Navigation Integration (`:feature:root` module)
* **Routes**: Add a new `Route.MovieDetail(val movieId: Int)` to `Route.kt`.
* **Wiring**: Update `Navigation.kt` to observe `onMovieSelected` from `MoviesFeature` and navigate to the `MovieDetailFeature`, similar to how the `Posts` feature operates.