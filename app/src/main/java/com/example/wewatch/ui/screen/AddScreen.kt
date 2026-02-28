package com.example.wewatch.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.example.wewatch.model.MovieDetails
import com.example.wewatch.viewmodel.MainViewModel
import com.example.wewatch.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onNavigateToSearch: (String, String?) -> Unit,
    onNavigateBack: () -> Unit,
    onMovieAdded: () -> Unit,
    searchViewModel: SearchViewModel,
    mainViewModel: MainViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var yearQuery by remember { mutableStateOf("") }
    val selectedMovie by searchViewModel.selectedMovieDetails.collectAsState()

    LaunchedEffect(selectedMovie) {
        selectedMovie?.let {
            searchQuery = it.Title
            yearQuery = it.Year
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить фильм") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Название фильма *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = yearQuery,
                onValueChange = { yearQuery = it },
                label = { Text("Год (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            onNavigateToSearch(searchQuery, yearQuery.takeIf { it.isNotBlank() })
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = searchQuery.isNotBlank()
                ) {
                    Icon(Icons.Default.Search, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Поиск")
                }
                Button(
                    onClick = {
                        selectedMovie?.let { details ->
                            mainViewModel.addMovie(
                                Movie(
                                    title = details.Title,
                                    year = details.Year,
                                    posterUrl = details.Poster,
                                    imdbID = details.imdbID
                                )
                            )
                            searchViewModel.clearSelectedMovie()
                            onMovieAdded()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedMovie != null
                ) {
                    Text("Add movie")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            selectedMovie?.let { MovieDetailsView(it) }
        }
    }
}

@Composable
fun MovieDetailsView(movie: MovieDetails) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.Poster)
                    .crossfade(true)
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(200.dp, 300.dp).padding(bottom = 16.dp),
                contentScale = ContentScale.Crop
            )
            Text(text = movie.Title, style = MaterialTheme.typography.titleLarge)
            Text(text = movie.Year, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Жанр: ${movie.Genre}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}