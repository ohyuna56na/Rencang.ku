package com.oyn.rencangku.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyn.rencangku.data.LoginRequest
import com.oyn.rencangku.data.LoginResponse
import com.oyn.rencangku.network.ApiService
import kotlinx.coroutines.launch

class SignInViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.login(
                    LoginRequest(email, password)
                )
                _loginResult.value = response
            } catch (e: Exception) {
                _loginResult.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
