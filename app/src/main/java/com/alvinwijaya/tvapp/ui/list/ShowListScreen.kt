package com.alvinwijaya.tvapp.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.alvinwijaya.tvapp.data.model.Show
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ShowListRoute(
    viewModel: ShowListViewModel,
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ShowListScreen(
        uiState = uiState,
        onRetry = viewModel::loadShows,
        onShowClick = onShowClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowListScreen(
    uiState: ShowListUiState,
    onRetry: () -> Unit,
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    val showScrollToTopButton by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex >= 4
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "TV Shows")
                }
            )
        },
        floatingActionButton = {
            if (
                uiState is ShowListUiState.Success &&
                showScrollToTopButton
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            gridState.animateScrollToItem(
                                index = 0
                            )
                        }
                    },
                    icon = {
                        Icon(
                            imageVector =
                                Icons.Default.KeyboardArrowUp,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(text = "Top")
                    },
                    containerColor =
                        MaterialTheme.colorScheme.primary,
                    contentColor =
                        MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        when (uiState) {
            ShowListUiState.Loading -> {
                LoadingContent(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is ShowListUiState.Error -> {
                ErrorContent(
                    message = uiState.message,
                    onRetry = onRetry,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is ShowListUiState.Success -> {
                ShowGrid(
                    shows = uiState.shows,
                    gridState = gridState,
                    onShowClick = onShowClick,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(
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
private fun ErrorContent(
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
private fun ShowGrid(
    shows: List<Show>,
    gridState: LazyGridState,
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (shows.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No TV shows found.")
        }

        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 12.dp,
            end = 12.dp,
            bottom = 104.dp
        ),
        horizontalArrangement =
            Arrangement.spacedBy(12.dp),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = shows,
            key = { show -> show.id }
        ) { show ->
            ShowCard(
                show = show,
                onClick = {
                    onShowClick(show.id)
                }
            )
        }
    }
}

@Composable
private fun ShowCard(
    show: Show,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posterUrl = show.image?.medium

    val ratingText =
        show.rating?.average?.let { rating ->
            String.format(
                Locale.getDefault(),
                "%.1f",
                rating
            )
        } ?: "N/A"

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            if (posterUrl != null) {
                AsyncImage(
                    model = posterUrl,
                    contentDescription =
                        "${show.name} poster",
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
                    Text(text = "No image")
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement =
                    Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = show.name,
                    modifier = Modifier.fillMaxWidth(),
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            lineHeight = 20.sp
                        ),
                    fontWeight = FontWeight.Bold,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Rating: $ratingText",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}