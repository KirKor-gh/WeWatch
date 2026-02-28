package com.example.wewatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wewatch.ui.screens.AddScreen
import com.example.wewatch.ui.screens.MainScreen
import com.example.wewatch.ui.screens.SearchScreen
import com.example.wewatch.ui.theme.WeWatchTheme
import com.example.wewatch.viewmodel.SearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeWatchTheme {
                val navController = rememberNavController()
                val searchViewModel: SearchViewModel = viewModel()
                var searchTerm by remember { mutableStateOf("") }
                var searchYear by remember { mutableStateOf<String?>(null) }

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            onNavigateToAdd = { navController.navigate("add") }
                        )
                    }
                    composable("add") {
                        AddScreen(
                            onNavigateToSearch = { term, year ->
                                searchTerm = term
                                searchYear = year
                                navController.navigate("search")
                            },
                            onNavigateBack = { navController.popBackStack() },
                            onMovieAdded = { navController.popBackStack() },
                            searchViewModel = searchViewModel
                        )
                    }
                    composable("search") {
                        SearchScreen(
                            searchTerm = searchTerm,
                            year = searchYear,
                            onNavigateBack = { navController.popBackStack() },
                            onMovieSelected = { imdbID ->
                                searchViewModel.getMovieDetails(imdbID)
                                navController.popBackStack()
                            },
                            searchViewModel = searchViewModel
                        )
                    }
                }
            }
        }
    }
}