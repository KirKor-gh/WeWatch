// app/src/main/java/com/example/movieapp/MainActivity.kt
package com.example.movieapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movieapp.model.Movie
import kotlinx.coroutines.launch
import com.example.movieapp.model.MovieRepository
import com.example.movieapp.ui.main.MainEffect
import com.example.movieapp.ui.main.MainIntent
import com.example.movieapp.ui.main.MainState
import com.example.movieapp.ui.main.MainViewModel
import com.example.movieapp.ui.main.Screen
import com.example.movieapp.view.AddScreen
import com.example.movieapp.view.MainScreen
import com.example.movieapp.view.SearchScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // Создаём репозиторий и ViewModel
                val repository = MovieRepository(applicationContext)
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(repository)
                )

                // Собираем состояние
                val state by viewModel.state.collectAsState()

                // Локальное состояние для выбранного фильма (дублируем из state для совместимости)
                var selectedMovieForEdit by rememberSaveable { mutableStateOf<Movie?>(null) }

                // Обрабатываем эффекты (одноразовые события)
                LaunchedEffect(Unit) {
                    viewModel.effect.collect { effect ->
                        handleEffect(effect) { newSelectedMovie ->
                            selectedMovieForEdit = newSelectedMovie
                        }
                    }
                }

                // Отображаем текущий экран с учётом навигации
                when (state.currentScreen) {
                    Screen.MAIN -> MainScreen(
                        state = state,
                        onIntent = viewModel::handleIntent
                    )

                    Screen.ADD -> AddScreen(
                        state = state.copy(selectedMovieForEdit = selectedMovieForEdit),
                        onIntent = viewModel::handleIntent
                    )

                    Screen.SEARCH -> SearchScreen(
                        state = state,
                        onIntent = viewModel::handleIntent
                    )
                }
            }
        }
    }

    private fun handleEffect(
        effect: MainEffect,
        onSelectedMovieUpdate: (Movie?) -> Unit
    ) {
        when (effect) {
            is MainEffect.NavigateToMain -> {
                // Возврат на главный экран
                // Состояние уже обновлено через ViewModel, ничего делать не нужно
            }

            is MainEffect.NavigateToAdd -> {
                // Навигация на экран добавления с выбранным фильмом
                onSelectedMovieUpdate(effect.movie)
                // Состояние currentScreen уже обновлено через ViewModel
            }

            is MainEffect.NavigateToSearch -> {
                // Навигация на экран поиска
                // Состояние уже обновлено через ViewModel
            }

            is MainEffect.NavigateBack -> {
                // Навигация назад
                // Состояние уже обновлено через ViewModel
            }

            is MainEffect.ShowError -> {
                // Показать тост с ошибкой
                Toast.makeText(this, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// Factory для ViewModel
class MainViewModelFactory(
    private val repository: MovieRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}