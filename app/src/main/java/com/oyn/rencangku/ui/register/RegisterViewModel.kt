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


                val existingUser = apiService.checkEmail(
                    apiKey = apiKey,
                    auth = auth,
                    email = email
                )

                if (existingUser.isNotEmpty()) {
                    onError("Email sudah terdaftar")
                    return@launch
                }

                val response = apiService.signup(
                    apiKey = apiKey,
                    auth = auth,
                    request = SignupRequest(name, email, password)
                )

                val user = response.firstOrNull()

                if (user != null) {

                    onSuccess(
                        AuthResponse(
                            authToken = user.id.toString(),
                            user = user
                        )
                    )

                } else {

                    onError("Gagal mengambil data user")
                }
            } catch (e: Exception) {

                e.printStackTrace()

                val errorMessage = when {

                    e.message?.contains("duplicate", true) == true ->
                        "Email sudah terdaftar"

                    e.message?.contains("End of input", true) == true ->
                        "Registrasi berhasil"

                    else ->
                        e.localizedMessage ?: "Signup gagal"
                }

                onError(errorMessage)
            }
        }
    }
}
