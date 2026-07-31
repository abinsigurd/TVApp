package com.alvinwijaya.tvapp.ui.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import com.alvinwijaya.tvapp.ui.components.AppTopBar
import com.alvinwijaya.tvapp.ui.list.components.ShowGrid
import com.alvinwijaya.tvapp.ui.list.components.ShowListError
import com.alvinwijaya.tvapp.ui.list.components.ShowListLoading
import kotlinx.coroutines.launch

@Composable
fun ShowListRoute(
    viewModel: ShowListViewModel,
    onShowClick: (Int) -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ShowListScreen(
        uiState = uiState,
        onRetry = viewModel::loadShows,
        onLoadMore = viewModel::loadNextPage,
        onShowClick = onShowClick,
        isDarkTheme = isDarkTheme,
        onThemeToggle = onThemeToggle,
        modifier = modifier
    )
}

@Composable
fun ShowListScreen(
    uiState: ShowListUiState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onShowClick: (Int) -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    val showScrollToTopButton by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex >= 6
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "TV Shows",
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
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
                            gridState.animateScrollToItem(index = 0)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            text = "Top",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        when (uiState) {
            ShowListUiState.Loading -> {
                ShowListLoading(
                    contentPadding = innerPadding
                )
            }

            is ShowListUiState.Error -> {
                ShowListError(
                    message = uiState.message,
                    onRetry = onRetry,
                    contentPadding = innerPadding
                )
            }

            is ShowListUiState.Success -> {
                ShowGrid(
                    shows = uiState.shows,
                    gridState = gridState,
                    contentPadding = innerPadding,
                    isLoadingMore = uiState.isLoadingMore,
                    loadMoreError = uiState.loadMoreError,
                    endReached = uiState.endReached,
                    onLoadMore = onLoadMore,
                    onShowClick = onShowClick
                )
            }
        }
    }
}