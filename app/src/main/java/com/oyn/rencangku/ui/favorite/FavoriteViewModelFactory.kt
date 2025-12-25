package com.oyn.rencangku.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FavoriteViewModelFactory(
    private val repository: FavoriteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteViewModel(repository) as T
    }
}
