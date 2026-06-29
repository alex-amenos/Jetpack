package com.alxnophis.jetpack.movies.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.alxnophis.jetpack.core.ui.composable.CoreTags
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.kotlin.constants.BREAK_LINE
import com.alxnophis.jetpack.kotlin.constants.BULLET
import com.alxnophis.jetpack.kotlin.constants.COLON
import com.alxnophis.jetpack.kotlin.constants.WHITE_SPACE
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import com.alxnophis.jetpack.movies.R
import com.alxnophis.jetpack.movies.domain.model.MovieDetails
import com.alxnophis.jetpack.movies.ui.composable.provider.MovieDetailStateProvider
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailEvent
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailState
import com.alxnophis.jetpack.movies.ui.mapper.toMessage
import java.util.Locale

@Composable
internal fun MovieDetailScreen(
    state: MovieDetailState,
    handleEvent: (MovieDetailEvent) -> Unit,
) {
    AppTheme {
        Scaffold { paddingValues ->
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
                        MovieErrorContent(
                            errorMessage = state.error.toMessage(),
                            modifier =
                                Modifier
                                    .padding(paddingValues)
                                    .align(Alignment.Center)
                                    .padding(32.dp),
                        )
                    }

                    state.movie != null -> {
                        MovieSuccessContent(
                            movie = state.movie,
                            paddingValues = paddingValues,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(CoreTags.TAG_MOVIE_DETAIL),
                        )
                    }
                }

                BackButtonOverlay(
                    onBack = { handleEvent(MovieDetailEvent.GoBackRequested) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .testTag(CoreTags.TAG_CORE_BACK),
                )
            }
        }
    }
}

@Composable
private fun MovieSuccessContent(
    movie: MovieDetails,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        val movieImage = movie.backdropPath ?: movie.posterPath
        movieImage?.let {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500$movieImage",
                contentDescription = movie.title,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
            )
        } ?: Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(80.dp),
        )
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 0.dp),
        ) {
            val details = mutableListOf<AnnotatedString>()
            val createDetail: (String) -> AnnotatedString = { text ->
                buildAnnotatedString {
                    append("$BULLET$WHITE_SPACE")
                    val colonIndex = text.indexOf(COLON)
                    if (colonIndex != -1) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(text.substring(ZERO_INT, colonIndex))
                        }
                        append(text.substring(colonIndex))
                    } else {
                        append(text)
                    }
                }
            }
            movie.releaseDate?.let {
                details.add(createDetail(stringResource(id = R.string.movies_released, it)))
            }
            movie.voteAverage?.let {
                val formattedRating = String.format(Locale.US, "%.1f", it)
                details.add(createDetail(stringResource(id = R.string.movies_rating, formattedRating)))
            }
            movie.runtime?.let {
                details.add(createDetail(stringResource(id = R.string.movies_runtime, it.toString())))
            }

            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (details.isNotEmpty()) {
                val joinedText =
                    buildAnnotatedString {
                        details.forEachIndexed { index, annotatedString ->
                            append(annotatedString)
                            if (index < details.lastIndex) {
                                append(BREAK_LINE)
                            }
                        }
                    }
                Text(
                    text = joinedText,
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
                        fontWeight = FontWeight.Bold,
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

@PreviewLightDark
@Composable
private fun MovieDetailScreenPreview(
    @PreviewParameter(MovieDetailStateProvider::class) state: MovieDetailState,
) {
    MovieDetailScreen(
        state = state,
        handleEvent = {},
    )
}
