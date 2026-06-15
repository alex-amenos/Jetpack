package com.alxnophis.jetpack.movies.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.alxnophis.jetpack.core.ui.composable.CoreErrorDialog
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.movies.R
import com.alxnophis.jetpack.movies.domain.model.MovieDetails
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailEvent
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailState

@Composable
internal fun MovieDetailScreen(
    state: MovieDetailState,
    handleEvent: (MovieDetailEvent) -> Unit,
) {
    AppTheme {
        Scaffold { _ ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    state.error != null -> {
                        CoreErrorDialog(
                            errorMessage = stringResource(id = R.string.movies_error_loading, state.error.toString()),
                            dismissError = { handleEvent(MovieDetailEvent.ErrorDismissRequested) },
                        )
                    }

                    state.movie != null -> {
                        MovieSuccessContent(movie = state.movie, modifier = Modifier.fillMaxSize())
                    }
                }

                BackButtonOverlay(
                    onBack = { handleEvent(MovieDetailEvent.GoBackRequested) },
                    modifier = Modifier.align(Alignment.TopStart),
                )
            }
        }
    }
}

@Composable
private fun MovieSuccessContent(
    movie: MovieDetails,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.backdropPath ?: movie.posterPath}",
            contentDescription = movie.title,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = movie.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))

            val details = mutableListOf<String>()
            movie.releaseDate?.let { details.add(stringResource(id = R.string.movies_released, it)) }
            movie.voteAverage?.let { details.add(stringResource(id = R.string.movies_rating, it.toString())) }
            movie.runtime?.let { details.add(stringResource(id = R.string.movies_runtime, it.toString())) }

            if (details.isNotEmpty()) {
                Text(
                    text = details.joinToString(" • "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            movie.tagline
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    Text(
                        text = "\"$it\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

            Text(text = stringResource(id = R.string.movies_overview), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = movie.overview, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BackButtonOverlay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .padding(
                    top =
                        WindowInsets.statusBars
                            .asPaddingValues()
                            .calculateTopPadding() + 8.dp,
                    start = 8.dp,
                ).clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                .clickable(onClick = onBack)
                .padding(8.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(id = R.string.movies_cd_go_back),
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}
