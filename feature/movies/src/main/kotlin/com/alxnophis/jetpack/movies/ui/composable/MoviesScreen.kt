package com.alxnophis.jetpack.movies.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import com.alxnophis.jetpack.movies.R
import com.alxnophis.jetpack.movies.domain.model.Movie
import com.alxnophis.jetpack.movies.domain.model.MovieException
import com.alxnophis.jetpack.movies.ui.composable.provider.MoviesPagingProvider
import com.alxnophis.jetpack.movies.ui.contract.MoviesEvent
import com.alxnophis.jetpack.movies.ui.contract.MoviesState
import com.alxnophis.jetpack.movies.ui.mapper.toMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoviesScreen(
    state: MoviesState,
    movies: LazyPagingItems<Movie>,
    handleEvent: (MoviesEvent) -> Unit,
) {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.movies_search_title)) },
                    navigationIcon = {
                        IconButton(onClick = { handleEvent(MoviesEvent.GoBackRequested) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.movies_cd_go_back),
                            )
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            scrolledContainerColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                )
            },
        ) { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.surface),
            ) {
                SearchField(
                    searchQuery = state.searchQuery,
                    onSearchQueryChanged = { handleEvent(MoviesEvent.SearchQueryChanged(it)) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(movies.itemCount) { index ->
                            val movie = movies[index]
                            if (movie != null) {
                                MovieItem(
                                    movie = movie,
                                    onClick = { handleEvent(MoviesEvent.MovieClicked(movie.id)) },
                                )
                            }
                        }

                        movies.apply {
                            when {
                                loadState.append is LoadState.Loading -> {
                                    appendLoadingContent()
                                }

                                loadState.append is LoadState.Error -> {
                                    val e = movies.loadState.append as LoadState.Error
                                    appendErrorContent(
                                        error = e.error,
                                        onRetry = { movies.retry() },
                                    )
                                }

                                itemCount > 0 && loadState.refresh is LoadState.Loading -> {
                                    appendLoadingContent()
                                }

                                itemCount > 0 && loadState.refresh is LoadState.Error -> {
                                    val e = movies.loadState.refresh as LoadState.Error
                                    appendErrorContent(
                                        error = e.error,
                                        onRetry = { movies.retry() },
                                    )
                                }
                            }
                        }
                    }

                    if (movies.itemCount == 0) {
                        movies.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }

                                loadState.refresh is LoadState.Error -> {
                                    val e = movies.loadState.refresh as LoadState.Error
                                    RefreshErrorContent(
                                        error = e.error,
                                        onRetry = { movies.retry() },
                                        modifier = Modifier.align(Alignment.Center),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        label = { Text(stringResource(id = R.string.movies_search_label)) },
        modifier = modifier,
        singleLine = true,
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChanged(EMPTY) }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.movies_cd_clear_search),
                    )
                }
            }
        },
    )
}

@Composable
private fun MovieItem(
    movie: Movie,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
            contentDescription = movie.title,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = movie.releaseDate?.take(4) ?: stringResource(id = R.string.movies_year_unknown),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun RefreshErrorContent(
    error: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorMessage = (error as? MovieException)?.error?.toMessage() ?: error.localizedMessage ?: stringResource(id = R.string.movies_error_unknown)
    MovieErrorContent(
        errorMessage = errorMessage,
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onRetry)
                .padding(32.dp),
    )
}

private fun LazyGridScope.appendLoadingContent() {
    item(span = { GridItemSpan(maxLineSpan) }) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

private fun LazyGridScope.appendErrorContent(
    error: Throwable,
    onRetry: () -> Unit,
) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        val errorMessage =
            (error as? MovieException)?.error?.toMessage() ?: error.localizedMessage ?: stringResource(
                id = R.string.movies_error_unknown,
            )
        MovieErrorContent(
            errorMessage = errorMessage,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onRetry)
                    .padding(32.dp),
        )
    }
}

@PreviewLightDark
@Composable
private fun MoviesScreenSuccessPreview() {
    val movies =
        MoviesPagingProvider()
            .values
            .elementAt(0)
            .collectAsLazyPagingItems()
    MoviesScreen(
        state = MoviesState(searchQuery = "Matrix"),
        movies = movies,
        handleEvent = {},
    )
}

@PreviewLightDark
@Composable
private fun MoviesScreenEmptyPreview() {
    val movies =
        MoviesPagingProvider()
            .values
            .elementAt(1)
            .collectAsLazyPagingItems()
    MoviesScreen(
        state = MoviesState(searchQuery = "Unknown"),
        movies = movies,
        handleEvent = {},
    )
}

@PreviewLightDark
@Composable
private fun MoviesScreenErrorPreview() {
    val movies =
        MoviesPagingProvider()
            .values
            .elementAt(2)
            .collectAsLazyPagingItems()
    MoviesScreen(
        state = MoviesState(searchQuery = "Supercalifragilisticexpialidocious"),
        movies = movies,
        handleEvent = {},
    )
}
