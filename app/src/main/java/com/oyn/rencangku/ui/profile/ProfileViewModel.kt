package com.oyn.rencangku.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.data.User
import com.oyn.rencangku.network.ApiService
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val token = sessionManager.getToken()

                if (token.isNullOrEmpty()) {
                    _error.value = "Session habis, silakan login ulang"
                    return@launch
                }

                val user = apiService.getProfile("Bearer $token")
                _user.value = user

            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal memuat data profil"
            }
        }
    }
}
