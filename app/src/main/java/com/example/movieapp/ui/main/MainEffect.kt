// app/src/main/java/com/example/movieapp/ui/main/MainEffect.kt
package com.example.movieapp.ui.main

import com.example.movieapp.model.Movie

sealed class MainEffect {
    object NavigateToMain : MainEffect()
    data class NavigateToAdd(val movie: Movie?) : MainEffect()
    object NavigateToSearch : MainEffect()
    object NavigateBack : MainEffect()
    data class ShowError(val message: String) : MainEffect()
}