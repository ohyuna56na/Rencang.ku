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

                val result = apiService.getProfile(
                    apiKey = ApiClient.API_KEY,
                    auth = "Bearer ${ApiClient.API_KEY}",
                    id = "eq.${sessionManager.getUserId()}"
                )

                if (result.isNotEmpty()) {
                    _user.value = result[0]
                } else {
                    _error.value = "User tidak ditemukan"
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _error.value =
                    e.message ?: "Gagal memuat profile"
            }
        }
    }
}
