// app/src/main/java/com/example/movieapp/model/MovieDetailsResponse.kt
package com.example.movieapp.model

data class MovieDetailsResponse(
    val Title: String,
    val Year: String,
    val Rated: String,
    val Released: String,
    val Runtime: String,
    val Genre: String,  // Здесь жанр
    val Director: String,
    val Writer: String,
    val Actors: String,
    val Plot: String,
    val Language: String,
    val Country: String,
    val Awards: String,
    val Poster: String,
    val imdbID: String,
    val Response: String,
    val Error: String?
)