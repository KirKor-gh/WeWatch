// app/src/main/java/com/example/movieapp/model/Movie.kt
package com.example.movieapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val year: String,
    val posterUrl: String,
    val imdbID: String,
    var genre: String? = null,  // Добавляем поле для жанра
    var isSelected: Boolean = false
)