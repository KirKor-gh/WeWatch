package com.example.wewatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatch.data.MovieDatabase
import com.example.wewatch.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MovieDatabase.getDatabase(application)
    private val movieDao = database.movieDao()

    val allMovies: Flow<List<Movie>> = movieDao.getAllMovies()

    private val _selectedMovieIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedMovieIds: StateFlow<Set<Long>> = _selectedMovieIds.asStateFlow()

    fun toggleSelection(movieId: Long) {
        val currentSet = _selectedMovieIds.value.toMutableSet()
        if (currentSet.contains(movieId)) {
            currentSet.remove(movieId)
        } else {
            currentSet.add(movieId)
        }
        _selectedMovieIds.value = currentSet
    }

    fun clearSelection() {
        _selectedMovieIds.value = emptySet()
    }

    fun deleteSelectedMovies() {
        viewModelScope.launch {
            if (_selectedMovieIds.value.isNotEmpty()) {
                movieDao.deleteMoviesByIds(_selectedMovieIds.value.toList())
                clearSelection()
            }
        }
    }

    fun addMovie(movie: Movie) {
        viewModelScope.launch {
            movieDao.insertMovie(movie)
        }
    }
}