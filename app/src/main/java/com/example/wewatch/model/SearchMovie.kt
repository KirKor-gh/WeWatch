package com.example.wewatch.model

data class SearchMovie(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Type: String,
    val Poster: String
)

data class SearchResponse(
    val Search: List<SearchMovie>?,
    val totalResults: String,
    val Response: String
)

data class MovieDetails(
    val Title: String,
    val Year: String,
    val Genre: String,
    val Poster: String,
    val imdbID: String
)