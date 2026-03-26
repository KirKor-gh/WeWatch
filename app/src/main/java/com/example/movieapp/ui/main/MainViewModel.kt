// app/src/main/java/com/example/movieapp/ui/main/MainViewModel.kt
package com.example.movieapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.Movie
import com.example.movieapp.model.MovieRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    // Состояние (State) — один Flow для всего экрана
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    // Эффекты (одноразовые события)
    private val _effect = MutableSharedFlow<MainEffect>()
    val effect: SharedFlow<MainEffect> = _effect.asSharedFlow()

    init {
        handleIntent(MainIntent.LoadMovies)
    }

    // Единая точка входа для всех интентов
    fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.LoadMovies -> loadMoviesFromDb()
            is MainIntent.SearchMovies -> searchMovies(intent.query)
            is MainIntent.ClearSearchResults -> clearSearchResults()
            is MainIntent.AddMovie -> addMovie(intent.movie)
            is MainIntent.NavigateToAdd -> navigateToAdd()
            is MainIntent.NavigateToEdit -> navigateToEdit(intent.movie)
            is MainIntent.ToggleMovieSelection -> toggleMovieSelection(intent.movie)
            is MainIntent.DeleteSelectedMovies -> deleteSelectedMovies()
            is MainIntent.ShowDeleteDialog -> showDeleteDialog()        // ← ДОБАВИТЬ ЭТУ СТРОКУ
            is MainIntent.ConfirmDelete -> confirmDelete()
            is MainIntent.DismissDeleteDialog -> dismissDeleteDialog()
            is MainIntent.NavigateBack -> navigateBack()
            is MainIntent.NavigateToSearch -> navigateToSearch()
        }
    }

    // ---- Обработчики интентов ----

    private fun loadMoviesFromDb() {
        viewModelScope.launch {
            repository.allMovies.collect { moviesList ->
                _state.update { currentState ->
                    currentState.copy(
                        movies = moviesList,
                        selectedCount = moviesList.count { it.isSelected }
                    )
                }
            }
        }
    }

    private fun searchMovies(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val results = repository.searchMovies(query)
                _state.update {
                    it.copy(
                        searchResults = results,
                        isLoading = false,
                        errorMessage = if (results.isEmpty()) "Фильмы не найдены" else null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Ошибка: ${e.message}"
                    )
                }
                _effect.emit(MainEffect.ShowError("Ошибка: ${e.message}"))
            }
        }
    }

    private fun clearSearchResults() {
        _state.update {
            it.copy(
                searchResults = emptyList(),
                errorMessage = null
            )
        }
    }

    private fun addMovie(movie: Movie) {
        viewModelScope.launch {
            repository.insertMovie(movie)
            // Обновляем состояние - переходим на главный экран
            _state.update {
                it.copy(
                    currentScreen = Screen.MAIN,
                    selectedMovieForEdit = null  // сбрасываем выбранный фильм
                )
            }
            // Отправляем эффект (на всякий случай)
            _effect.emit(MainEffect.NavigateToMain)
        }
    }

    private fun toggleMovieSelection(movie: Movie) {
        viewModelScope.launch {
            repository.updateMovie(movie.copy(isSelected = !movie.isSelected))
            // Состояние обновится через loadMoviesFromDb (подписка на allMovies)
        }
    }

    private fun showDeleteDialog() {                                    // ← ДОБАВИТЬ ЭТОТ МЕТОД
        _state.update { it.copy(isDeleteDialogVisible = true) }
    }

    private fun confirmDelete() {
        viewModelScope.launch {
            try {
                repository.deleteSelectedMovies()  // удаляем выбранные фильмы
                _state.update {
                    it.copy(
                        isDeleteDialogVisible = false,
                        // selectedCount обновится через подписку allMovies
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isDeleteDialogVisible = false) }
                _effect.emit(MainEffect.ShowError("Ошибка при удалении: ${e.message}"))
            }
        }
    }

    private fun dismissDeleteDialog() {
        _state.update { it.copy(isDeleteDialogVisible = false) }
    }

    private fun deleteSelectedMovies() {
        // Этот метод может быть не нужен, если используешь showDeleteDialog + confirmDelete
        // Но оставим для обратной совместимости
        viewModelScope.launch {
            repository.deleteSelectedMovies()
        }
    }

    private fun navigateToAdd() {
        viewModelScope.launch {
            _state.update { it.copy(currentScreen = Screen.ADD) }
            _effect.emit(MainEffect.NavigateToAdd(null))
        }
    }

    private fun navigateToEdit(movie: Movie) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    currentScreen = Screen.ADD,
                    selectedMovieForEdit = movie
                )
            }
            _effect.emit(MainEffect.NavigateToAdd(movie))
        }
    }

    private fun navigateToSearch() {
        viewModelScope.launch {
            _state.update { it.copy(currentScreen = Screen.SEARCH) }
            _effect.emit(MainEffect.NavigateToSearch)
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            when (_state.value.currentScreen) {
                Screen.ADD -> _state.update {
                    it.copy(
                        currentScreen = Screen.MAIN,
                        selectedMovieForEdit = null
                    )
                }
                Screen.SEARCH -> _state.update {
                    it.copy(
                        currentScreen = Screen.ADD
                    )
                }
                else -> _state.update { it.copy(currentScreen = Screen.MAIN) }
            }
            _effect.emit(MainEffect.NavigateBack)
        }
    } }