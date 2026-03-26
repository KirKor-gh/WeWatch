// app/src/main/java/com/example/movieapp/view/SearchScreen.kt
package com.example.movieapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.movieapp.model.Movie
import com.example.movieapp.ui.main.MainIntent
import com.example.movieapp.ui.main.MainState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: MainState,
    onIntent: (MainIntent) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showContextMenu by remember { mutableStateOf(false) }
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }

    // Очищаем результаты при выходе
    DisposableEffect(Unit) {
        onDispose {
            onIntent(MainIntent.ClearSearchResults)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Поиск фильмов") },
                navigationIcon = {
                    IconButton(onClick = { onIntent(MainIntent.NavigateBack) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Поисковая строка
            SearchBar(
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        onIntent(MainIntent.SearchMovies(searchQuery))
                    }
                },
                isLoading = state.isLoading
            )

            HorizontalDivider()

            // Результаты поиска
            SearchResultsContent(
                state = state,
                onMovieClick = { movie ->
                    onIntent(MainIntent.NavigateToEdit(movie))
                },
                onMovieLongClick = { movie ->
                    selectedMovie = movie
                    showContextMenu = true
                }
            )
        }
    }

    // Контекстное меню
    if (showContextMenu && selectedMovie != null) {
        MovieContextMenu(
            movie = selectedMovie!!,
            onDismiss = { showContextMenu = false },
            onEdit = {
                onIntent(MainIntent.NavigateToEdit(selectedMovie!!))
                showContextMenu = false
            }
        )
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Введите название фильма") },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            }
        )

        Button(
            onClick = onSearch,
            enabled = searchQuery.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Найти")
            }
        }
    }
}


@Composable
fun SearchResultsContent(
    modifier: Modifier = Modifier,
    state: MainState,
    onMovieClick: (Movie) -> Unit,
    onMovieLongClick: (Movie) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        when {
            state.isLoading -> {
                LoadingIndicator()
            }

            state.errorMessage != null -> {
                ErrorMessage(
                    message = state.errorMessage!!
                )
            }

            state.searchResults.isEmpty() && !state.isLoading -> {
                EmptySearchContent()
            }

            else -> {
                SearchResultsList(
                    results = state.searchResults,
                    onMovieClick = onMovieClick,
                    onMovieLongClick = onMovieLongClick
                )
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Поиск фильмов...",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Попробуйте другой запрос",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptySearchContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Введите название фильма",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Например: Avatar, Inception, Titanic",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SearchResultsList(
    results: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    onMovieLongClick: (Movie) -> Unit
) {
    // В SearchScreen.kt, внутри SearchResultsList:

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(
            items = results,
            key = { index, movie -> "${movie.imdbID}_$index" }  // ← уникальный ключ
        ) { index, movie ->
            SearchResultItem(
                movie = movie,
                onClick = { onMovieClick(movie) },
                onLongClick = { onMovieLongClick(movie) }
            )
        }
    }
}

@Composable
fun SearchResultItem(
    movie: Movie,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Заголовок и год
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = movie.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onLongClick) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Детали",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Информация о фильме
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Год выпуска
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = movie.year,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Жанр
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = movie.genre ?: "Драма",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // IMDb ID
            Text(
                text = "IMDb: ${movie.imdbID}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun MovieContextMenu(
    movie: Movie,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(movie.title) },
        text = {
            Column {
                Text("Год: ${movie.year}")
                Text("Жанр: ${movie.genre ?: "Не указан"}")
                Text("IMDb ID: ${movie.imdbID}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Выберите действие:")
            }
        },
        confirmButton = {
            TextButton(onClick = onEdit) {
                Text("Редактировать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}