package com.oyn.rencangku.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyn.rencangku.data.AuthResponse
import com.oyn.rencangku.data.SignupRequest
import com.oyn.rencangku.network.ApiService
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val apiService: ApiService
) : ViewModel() {

    fun signup(
        name: String,
        email: String,
        password: String,
        onSuccess: (AuthResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = apiService.signup(
                    SignupRequest(name, email, password)
                )
                onSuccess(response)
            } catch (e: Exception) {
                onError(e.message ?: "Signup gagal")
            }
        }
    }
}
