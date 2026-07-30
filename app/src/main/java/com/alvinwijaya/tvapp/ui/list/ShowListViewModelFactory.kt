package com.alvinwijaya.tvapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alvinwijaya.tvapp.data.repository.ShowRepository

class ShowListViewModelFactory(
    private val repository: ShowRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(
                ShowListViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return ShowListViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}