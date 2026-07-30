package com.alvinwijaya.tvapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvinwijaya.tvapp.data.repository.ShowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowListViewModel(
    private val repository: ShowRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ShowListUiState>(ShowListUiState.Loading)

    val uiState: StateFlow<ShowListUiState> =
        _uiState.asStateFlow()

    init {
        loadShows()
    }

    fun loadShows() {
        viewModelScope.launch {
            _uiState.value = ShowListUiState.Loading

            _uiState.value = try {
                val shows = repository.getShows()

                ShowListUiState.Success(
                    shows = shows
                )
            } catch (exception: Exception) {
                ShowListUiState.Error(
                    message = exception.message
                        ?: "Unable to load TV shows."
                )
            }
        }
    }
}