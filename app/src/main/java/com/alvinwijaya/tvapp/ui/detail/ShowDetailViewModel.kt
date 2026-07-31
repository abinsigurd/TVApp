package com.alvinwijaya.tvapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvinwijaya.tvapp.data.repository.ShowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowDetailViewModel(
    private val repository: ShowRepository,
    private val showId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShowDetailUiState>(
        ShowDetailUiState.Loading
    )

    val uiState: StateFlow<ShowDetailUiState> =
        _uiState.asStateFlow()

    init {
        loadShow()
    }

    fun loadShow() {
        viewModelScope.launch {
            _uiState.value = ShowDetailUiState.Loading

            _uiState.value = try {
                val content = repository.getShowDetailContent(
                    showId = showId
                )

                ShowDetailUiState.Success(
                    content = content
                )
            } catch (exception: Exception) {
                ShowDetailUiState.Error(
                    message = exception.message
                        ?: "Unable to load show details."
                )
            }
        }
    }
}