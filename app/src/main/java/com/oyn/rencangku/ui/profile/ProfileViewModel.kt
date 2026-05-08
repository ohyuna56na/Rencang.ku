package com.oyn.rencangku.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.data.User
import com.oyn.rencangku.network.ApiClient
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
                val userId = sessionManager.getUserId()
                if (userId == -1) {
                    _error.value = "Session habis"
                    return@launch
                }

                val apiKey = ApiClient.API_KEY
                val auth = "Bearer $apiKey"

                val result = apiService.getProfile(
                    apiKey = apiKey,
                    auth = auth,
                    id = sessionManager.getUserId().toString()
                )

                _user.value = result.firstOrNull()

            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal memuat data profil"
            }
        }
    }
}
