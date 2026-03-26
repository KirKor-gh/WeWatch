// app/src/main/java/com/example/movieapp/ui/main/MainState.kt
package com.example.movieapp.ui.main

import com.example.movieapp.model.Movie

data class MainState(
    val movies: List<Movie> = emptyList(),
    val searchResults: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCount: Int = 0,
    val isDeleteDialogVisible: Boolean = false,
    val currentScreen: Screen = Screen.MAIN,
    val selectedMovieForEdit: Movie? = null  // ← ЭТО ПОЛЕ ДОЛЖНО БЫТЬ
)

enum class Screen {
    MAIN, ADD, SEARCH
}