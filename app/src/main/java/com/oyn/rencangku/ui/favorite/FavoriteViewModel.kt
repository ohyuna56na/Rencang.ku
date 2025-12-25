package com.oyn.rencangku.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyn.rencangku.data.Favorite
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val repository: FavoriteRepository
) : ViewModel() {

    private val _favorites = MutableLiveData<List<Favorite>>()
    val favorites: LiveData<List<Favorite>> = _favorites

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _favorites.value = repository.getFavorites()
            } catch (e: Exception) {
                _favorites.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteFavorite(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteFavorite(id)
                loadFavorites() // refresh setelah delete
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
