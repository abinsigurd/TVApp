package com.alvinwijaya.tvapp.ui.detail

import com.alvinwijaya.tvapp.data.model.Show

sealed interface ShowDetailUiState {

    data object Loading : ShowDetailUiState

    data class Success(
        val show: Show
    ) : ShowDetailUiState

    data class Error(
        val message: String
    ) : ShowDetailUiState
}