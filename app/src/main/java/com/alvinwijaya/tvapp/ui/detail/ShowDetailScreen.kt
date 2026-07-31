package com.alvinwijaya.tvapp.ui.detail

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alvinwijaya.tvapp.data.model.Show
import com.alvinwijaya.tvapp.ui.components.AppTopBar

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
        is ShowDetailUiState.Success -> uiState.content.show
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    loadedShow?.let { show ->
                        IconButton(
                            onClick = {
                                shareShow(
                                    context = context,
                                    show = show
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
                ShowDetailBody(
                    content = uiState.content,
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
        CircularProgressIndicator()

        Text(
            text = "Loading details...",
            modifier = Modifier.padding(top = 16.dp),
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
            modifier = Modifier.widthIn(max = 380.dp),
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
        putExtra(Intent.EXTRA_SUBJECT, show.name)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    context.startActivity(
        Intent.createChooser(
            shareIntent,
            "Share TV show"
        )
    )
}