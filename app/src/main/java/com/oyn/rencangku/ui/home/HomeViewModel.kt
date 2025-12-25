package com.oyn.rencangku.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyn.rencangku.data.CulinaryPlace
import com.oyn.rencangku.weather.ApiConfig
import com.oyn.rencangku.weather.WeatherResponse
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse?>()
    val weatherData: LiveData<WeatherResponse?> = _weatherData

    private val _locationName = MutableLiveData<String>()
    val locationName: LiveData<String> = _locationName

    private val repository = HomeRepository()
    private val _culinaryPlaces =
        MutableLiveData<List<CulinaryPlace>>()
    val culinaryPlaces: LiveData<List<CulinaryPlace>> =
        _culinaryPlaces

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCulinaryPlaces() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = repository.getCulinaryPlaces()
                _culinaryPlaces.value = data
            } catch (e: Exception) {
                e.printStackTrace()
                _culinaryPlaces.value = emptyList()
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
                val response = ApiConfig.weatherService.getWeatherByCoord(lat, lon)
                _weatherData.value = response
            } catch (e: Exception) {
                e.printStackTrace()
                _culinaryPlaces.value = emptyList()
            }
        }
    }
}
