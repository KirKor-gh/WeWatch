// app/src/main/java/com/example/movieapp/view/MovieView.kt
package com.example.movieapp.view

import com.example.movieapp.model.Movie

interface MovieView {
    fun showMovies(movies: List<Movie>)
    fun showSearchResults(results: List<Movie>)
    fun showLoading(isLoading: Boolean)
    fun showError(message: String?)
    fun updateSelectedCount(count: Int)
    fun navigateToAdd(movie: Movie?)
    fun navigateToSearch()
    fun navigateBack()
}