// app/src/main/java/com/example/movieapp/model/MovieResponse.kt
package com.example.movieapp.model

data class MovieResponse(
    val Search: List<MovieSearchResult>?,
    val totalResults: String?,
    val Response: String,
    val Error: String?
)

data class MovieSearchResult(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Type: String,
    val Poster: String
)