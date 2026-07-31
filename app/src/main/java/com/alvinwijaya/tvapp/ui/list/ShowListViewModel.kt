package com.alvinwijaya.tvapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvinwijaya.tvapp.data.repository.ShowRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowListViewModel(
    private val repository: ShowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShowListUiState>(
        ShowListUiState.Loading
    )

    val uiState: StateFlow<ShowListUiState> =
        _uiState.asStateFlow()

    private var nextPage = FIRST_PAGE
    private var requestInProgress = false

    init {
        loadShows()
    }

    fun loadShows() {
        if (requestInProgress) {
            return
        }

        viewModelScope.launch {
            requestInProgress = true
            nextPage = FIRST_PAGE
            _uiState.value = ShowListUiState.Loading

            try {
                val firstPage = repository.getShows(
                    page = FIRST_PAGE
                )

                if (!firstPage.endReached) {
                    nextPage = FIRST_PAGE + 1
                }

                _uiState.value = ShowListUiState.Success(
                    shows = firstPage.shows,
                    endReached = firstPage.endReached
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = ShowListUiState.Error(
                    message = exception.message
                        ?.takeIf { it.isNotBlank() }
                        ?: "Unable to load TV shows."
                )
            } finally {
                requestInProgress = false
            }
        }
    }

    fun loadNextPage() {
        val currentState =
            _uiState.value as? ShowListUiState.Success
                ?: return

        if (
            requestInProgress ||
            currentState.endReached
        ) {
            return
        }

        viewModelScope.launch {
            requestInProgress = true

            _uiState.value = currentState.copy(
                isLoadingMore = true,
                loadMoreError = null
            )

            try {
                val nextShowPage = repository.getShows(
                    page = nextPage
                )

                if (nextShowPage.endReached) {
                    _uiState.value = currentState.copy(
                        isLoadingMore = false,
                        loadMoreError = null,
                        endReached = true
                    )

                    return@launch
                }

                val combinedShows = (
                        currentState.shows +
                                nextShowPage.shows
                        )
                    .distinctBy { show ->
                        show.id
                    }

                nextPage += 1

                _uiState.value = currentState.copy(
                    shows = combinedShows,
                    isLoadingMore = false,
                    loadMoreError = null,
                    endReached = false
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = currentState.copy(
                    isLoadingMore = false,
                    loadMoreError = exception.message
                        ?.takeIf { it.isNotBlank() }
                        ?: "Unable to load more shows."
                )
            } finally {
                requestInProgress = false
            }
        }
    }

    private companion object {
        const val FIRST_PAGE = 0
    }
}