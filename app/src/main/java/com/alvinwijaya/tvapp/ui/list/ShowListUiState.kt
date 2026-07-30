package com.alvinwijaya.tvapp.ui.list

import com.alvinwijaya.tvapp.data.model.Show

sealed interface ShowListUiState {

    data object Loading : ShowListUiState

    data class Success(
        val shows: List<Show>
    ) : ShowListUiState

    data class Error(
        val message: String
    ) : ShowListUiState
}