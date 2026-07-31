package com.alvinwijaya.tvapp.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.alvinwijaya.tvapp.data.model.Show

@Composable
fun ShowDetailRoute(
    viewModel: ShowDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ShowDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::loadShow,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    uiState: ShowDetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Show Details")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
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
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DetailErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = message,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Retry")
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

    val premiereText =
        show.premiered?.takeIf { it.isNotBlank() }
            ?: "Not available"

    val summaryText =
        show.summary
            ?.toPlainText()
            ?.takeIf { it.isNotBlank() }
            ?: "No summary available."

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            if (posterUrl != null) {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = "${show.name} poster",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(210f / 295f),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(210f / 295f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No image available")
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = show.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Premiered: $premiereText",
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider()

                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = summaryText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

private fun String.toPlainText(): String {
    return HtmlCompat.fromHtml(
        this,
        HtmlCompat.FROM_HTML_MODE_LEGACY
    ).toString().trim()
}