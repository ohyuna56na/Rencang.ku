package com.oyn.rencangku.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyn.rencangku.data.AuthResponse
import com.oyn.rencangku.data.SignupRequest
import com.oyn.rencangku.network.ApiClient
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
                val apiKey = ApiClient.API_KEY
                val auth = "Bearer $apiKey"

                val response = apiService.signup(
                    apiKey = apiKey,
                    auth = auth,
                    request = SignupRequest(name, email, password)
                )

                onSuccess(
                    AuthResponse(
                        authToken = response.id.toString(),
                        user = response
                    )
                )
            } catch (e: Exception) {
                onError(e.message ?: "Signup gagal")
            }
        }
    }
}
