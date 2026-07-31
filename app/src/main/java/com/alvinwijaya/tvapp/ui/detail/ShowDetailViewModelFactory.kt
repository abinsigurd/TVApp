package com.alvinwijaya.tvapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alvinwijaya.tvapp.data.repository.ShowRepository

class ShowDetailViewModelFactory(
    private val repository: ShowRepository,
    private val showId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                ShowDetailViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return ShowDetailViewModel(
                repository = repository,
                showId = showId
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}