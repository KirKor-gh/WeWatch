package com.example.wewatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatch.api.RetrofitClient
import com.example.wewatch.model.MovieDetails
import com.example.wewatch.model.SearchMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    private val apiService = RetrofitClient.apiService
    private val apiKey = RetrofitClient.getApiKey()

    private val _searchResults = MutableStateFlow<List<SearchMovie>>(emptyList())
    val searchResults: StateFlow<List<SearchMovie>> = _searchResults.asStateFlow()

    private val _selectedMovieDetails = MutableStateFlow<MovieDetails?>(null)
    val selectedMovieDetails: StateFlow<MovieDetails?> = _selectedMovieDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun searchMovies(searchTerm: String, year: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.searchMovies(apiKey, searchTerm, year)
                }
                if (response.Response == "True") {
                    _searchResults.value = response.Search ?: emptyList()
                } else {
                    _searchResults.value = emptyList()
                    _error.value = "Фильмы не найдены"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMovieDetails(imdbID: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val details = withContext(Dispatchers.IO) {
                    apiService.getMovieDetails(apiKey, imdbID)
                }
                _selectedMovieDetails.value = details
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки деталей: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSelectedMovie() {
        _selectedMovieDetails.value = null
    }
}