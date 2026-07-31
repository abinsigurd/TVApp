package com.alvinwijaya.tvapp.ui.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alvinwijaya.tvapp.data.model.Show
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@Composable
internal fun ShowGrid(
    shows: List<Show>,
    gridState: LazyGridState,
    contentPadding: PaddingValues,
    isLoadingMore: Boolean,
    loadMoreError: String?,
    endReached: Boolean,
    onLoadMore: () -> Unit,
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    PaginationLoadEffect(
        gridState = gridState,
        showCount = shows.size,
        isLoadingMore = isLoadingMore,
        loadMoreError = loadMoreError,
        endReached = endReached,
        onLoadMore = onLoadMore
    )

    if (shows.isEmpty()) {
        EmptyShowListContent(
            contentPadding = contentPadding,
            loadMoreError = loadMoreError,
            endReached = endReached,
            onRetry = onLoadMore,
            modifier = modifier
        )

        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            end = 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 104.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = shows,
            key = { show ->
                show.id
            }
        ) { show ->
            ShowCard(
                show = show,
                onClick = {
                    onShowClick(show.id)
                }
            )
        }

        when {
            isLoadingMore -> {
                item(
                    key = "pagination-loading",
                    span = {
                        GridItemSpan(maxLineSpan)
                    }
                ) {
                    LoadingMoreFooter()
                }
            }

            loadMoreError != null -> {
                item(
                    key = "pagination-error",
                    span = {
                        GridItemSpan(maxLineSpan)
                    }
                ) {
                    LoadMoreErrorFooter(
                        message = loadMoreError,
                        onRetry = onLoadMore
                    )
                }
            }

            endReached -> {
                item(
                    key = "pagination-end",
                    span = {
                        GridItemSpan(maxLineSpan)
                    }
                ) {
                    EndReachedFooter()
                }
            }
        }
    }
}

@Composable
private fun PaginationLoadEffect(
    gridState: LazyGridState,
    showCount: Int,
    isLoadingMore: Boolean,
    loadMoreError: String?,
    endReached: Boolean,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(
        showCount,
        isLoadingMore,
        loadMoreError,
        endReached
    ) {
        if (
            isLoadingMore ||
            loadMoreError != null ||
            endReached
        ) {
            return@LaunchedEffect
        }

        snapshotFlow {
            val layoutInfo = gridState.layoutInfo

            val lastVisibleItemIndex = layoutInfo
                .visibleItemsInfo
                .lastOrNull()
                ?.index
                ?: -1

            val totalItemsCount = layoutInfo.totalItemsCount

            totalItemsCount > 0 &&
                    lastVisibleItemIndex >=
                    totalItemsCount - LOAD_MORE_THRESHOLD
        }
            .distinctUntilChanged()
            .filter { shouldLoadMore ->
                shouldLoadMore
            }
            .first()

        onLoadMore()
    }
}

private const val LOAD_MORE_THRESHOLD = 6