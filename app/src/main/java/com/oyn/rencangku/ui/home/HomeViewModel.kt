package com.oyn.rencangku.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyn.rencangku.auth.SessionManager
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.data.User
import com.oyn.rencangku.network.ApiClient
import com.oyn.rencangku.network.ApiService
import com.oyn.rencangku.weather.ApiConfig
import com.oyn.rencangku.weather.WeatherResponse
import kotlinx.coroutines.launch

class HomeViewModel(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse?>()
    val weatherData: LiveData<WeatherResponse?> = _weatherData

    private val _locationName = MutableLiveData<String>()
    val locationName: LiveData<String> = _locationName

    private val repository = HomeRepository(sessionManager)

    private val _culinaryPlaces = MutableLiveData<List<CulinaryPlace>>()
    val culinaryPlaces: LiveData<List<CulinaryPlace>> = _culinaryPlaces

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var allRestaurant = listOf<CulinaryPlace>()

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    fun getUsername() {
        viewModelScope.launch {
            try {
                val apiKey = ApiClient.API_KEY
                val auth = "Bearer $apiKey"
                val userId = sessionManager.getUserId()
                if (userId == -1) {
                    _error.value = "Session habis"
                    return@launch
                }

                val response = apiService.getProfile(
                    apiKey = apiKey,
                    auth = auth,
                    id = "eq.$userId"
                )

                if (response.isNotEmpty()) {
                    _user.value = response[0]
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal mengambil data user"
            }
        }
    }

    fun loadCulinaryPlaces() {
        viewModelScope.launch {

            _isLoading.value = true

            try {

                allRestaurant = repository.getCulinaryPlaces()

                _culinaryPlaces.value = allRestaurant

                _categories.value =
                    allRestaurant
                        .filter { it.displayCategory.isNotBlank() }
                        .groupBy { it.displayCategory }
                        .toList()
                        .sortedByDescending { (_, restaurants) ->
                            restaurants.size
                        }
                        .map { (category, _) ->
                            category
                        }

            } catch (e: Exception) {

                _culinaryPlaces.value = emptyList()

            } finally {

                _isLoading.value = false

            }
        }
    }

    fun loadCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _culinaryPlaces.value = repository.getCategory(category)
            } catch (e: Exception) {
                _culinaryPlaces.value = emptyList()
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setLocation(city: String) {
        _locationName.value = city
    }

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                _weatherData.value =
                    ApiConfig.weatherService.getWeatherByCoord(lat, lon)
            } catch (_: Exception) {}
        }
    }
}
