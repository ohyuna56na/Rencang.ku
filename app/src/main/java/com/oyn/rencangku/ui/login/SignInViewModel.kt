package com.oyn.rencangku.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyn.rencangku.data.LoginResponse
import com.oyn.rencangku.data.User
import com.oyn.rencangku.network.ApiClient
import com.oyn.rencangku.network.ApiService
import kotlinx.coroutines.launch

class SignInViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val apiKey = ApiClient.API_KEY
                val auth = "Bearer $apiKey"

                val result = apiService.login(
                    apiKey = apiKey,
                    auth = auth,
                    email = "eq.$email",
                    password = "eq.$password"
                )

                _loginResult.value =
                    if (result.isNotEmpty()) result[0] else null

            } catch (e: Exception) {
                e.printStackTrace()
                _loginResult.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
