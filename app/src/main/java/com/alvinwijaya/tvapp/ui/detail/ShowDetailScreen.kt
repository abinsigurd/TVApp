package com.alvinwijaya.tvapp.ui.detail

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.alvinwijaya.tvapp.data.model.Show
import com.alvinwijaya.tvapp.ui.components.AppTopBar
import java.util.Locale

@Composable
fun ShowDetailRoute(
    viewModel: ShowDetailViewModel,
    onBackClick: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ShowDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::loadShow,
        isDarkTheme = isDarkTheme,
        onThemeToggle = onThemeToggle,
        modifier = modifier
    )
}

@Composable
fun ShowDetailScreen(
    uiState: ShowDetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val loadedShow = when (uiState) {
        is ShowDetailUiState.Success -> uiState.show
        else -> null
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Show Details",
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    if (loadedShow != null) {
                        IconButton(
                            onClick = {
                                shareShow(
                                    context = context,
                                    show = loadedShow
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share show"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            ShowDetailUiState.Loading -> {
                DetailLoadingContent(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is ShowDetailUiState.Error -> {
                DetailErrorContent(
                    message = uiState.message,
                    onRetry = onRetry,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is ShowDetailUiState.Success -> {
                DetailSuccessContent(
                    show = uiState.show,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun DetailLoadingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Loading details...",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DetailErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Unable to load details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onRetry,
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    Text(text = "Try again")
                }
            }
        }
    }
}

@Composable
private fun DetailSuccessContent(
    show: Show,
    modifier: Modifier = Modifier
) {
    val posterUrl =
        show.image?.original ?: show.image?.medium

    val premiereText = show.premiered
        ?.takeIf { it.isNotBlank() }
        ?: "Not available"

    val ratingText = show.rating?.average?.let { rating ->
        String.format(
            Locale.getDefault(),
            "%.1f",
            rating
        )
    } ?: "N/A"

    val summaryText = show.summary
        ?.toPlainText()
        ?.takeIf { it.isNotBlank() }
        ?: "No summary available."

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = 32.dp
        )
    ) {
        item {
            DetailPoster(
                posterUrl = posterUrl,
                showName = show.name
            )
        }

        item {
            Column(
                modifier = Modifier.padding(
                    start = 20.dp,
                    top = 20.dp,
                    end = 20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = show.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetadataChip(
                        text = "★ $ratingText",
                        highlighted = true
                    )

                    MetadataChip(
                        text = "Premiered $premiereText"
                    )
                }

                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = summaryText,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailPoster(
    posterUrl: String?,
    showName: String,
    modifier: Modifier = Modifier
) {
    val posterModifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .aspectRatio(2f / 3f)
        .clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)

    if (posterUrl == null) {
        Box(
            modifier = posterModifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No poster available",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        return
    }

    SubcomposeAsyncImage(
        model = posterUrl,
        contentDescription = "$showName poster",
        modifier = posterModifier,
        contentScale = ContentScale.Crop,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
            }
        },
        error = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Unable to load poster",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
private fun MetadataChip(
    text: String,
    highlighted: Boolean = false
) {
    val containerColor = if (highlighted) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val contentColor = if (highlighted) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 7.dp
            ),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun shareShow(
    context: Context,
    show: Show
) {
    val summary = show.summary
        ?.toPlainText()
        ?.takeIf { it.isNotBlank() }
        ?: "No summary available."

    val showUrl = show.url
        ?.takeIf { it.isNotBlank() }
        ?: "https://api.tvmaze.com/shows/${show.id}"

    val shareText = buildString {
        appendLine(show.name)
        appendLine()
        appendLine(summary)
        appendLine()
        append(showUrl)
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_SUBJECT,
            show.name
        )
        putExtra(
            Intent.EXTRA_TEXT,
            shareText
        )
    }

    context.startActivity(
        Intent.createChooser(
            shareIntent,
            "Share TV show"
        )
    )
}

private fun String.toPlainText(): String {
    return HtmlCompat.fromHtml(
        this,
        HtmlCompat.FROM_HTML_MODE_LEGACY
    ).toString().trim()
}