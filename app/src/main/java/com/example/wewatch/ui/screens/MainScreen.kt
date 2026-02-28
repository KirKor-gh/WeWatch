package com.example.wewatch.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.wewatch.model.Movie
import com.example.wewatch.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToAdd: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val movies by viewModel.allMovies.collectAsState(initial = emptyList())
    val selectedIds by viewModel.selectedMovieIds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фильмы к просмотру") },
                actions = {
                    if (selectedIds.isNotEmpty()) {
                        IconButton(onClick = { viewModel.deleteSelectedMovies() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { paddingValues ->
        if (movies.isEmpty()) {
            EmptyView(modifier = Modifier.padding(paddingValues))
        } else {
            MovieList(
                movies = movies,
                selectedIds = selectedIds,
                onMovieClick = {},
                onCheckboxClick = { movie, isChecked ->
                    viewModel.toggleSelection(movie.id)
                },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun EmptyView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(android.R.drawable.gallery_thumb)
                .crossfade(true)
                .build()
        )
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(150.dp, 200.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Нет выбранных фильмов", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun MovieList(
    movies: List<Movie>,
    selectedIds: Set<Long>,
    onMovieClick: (Movie) -> Unit,
    onCheckboxClick: (Movie, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(movies) { movie ->
            MovieItem(
                movie = movie,
                isSelected = selectedIds.contains(movie.id),
                onMovieClick = { onMovieClick(movie) },
                onCheckboxClick = { isChecked -> onCheckboxClick(movie, isChecked) }
            )
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    isSelected: Boolean,
    onMovieClick: () -> Unit,
    onCheckboxClick: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.posterUrl)
                    .crossfade(true)
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(60.dp, 80.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(text = movie.title, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Text(text = movie.year, style = MaterialTheme.typography.bodyMedium)
            }
            Checkbox(checked = isSelected, onCheckedChange = onCheckboxClick)
        }
    }
}