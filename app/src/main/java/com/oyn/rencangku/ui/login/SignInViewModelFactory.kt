package com.oyn.rencangku.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oyn.rencangku.network.ApiService

class SignInViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignInViewModel(apiService) as T
    }
}
