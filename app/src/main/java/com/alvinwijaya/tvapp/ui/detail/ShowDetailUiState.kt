package com.alvinwijaya.tvapp.ui.detail

import com.alvinwijaya.tvapp.data.model.ShowDetailContent

sealed interface ShowDetailUiState {

    data object Loading : ShowDetailUiState

    data class Success(
        val content: ShowDetailContent,
        val selectedSeasonNumber: Int?
    ) : ShowDetailUiState

    data class Error(
        val message: String
    ) : ShowDetailUiState
}