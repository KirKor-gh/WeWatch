package com.example.wewatch.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val year: String,
    val posterUrl: String,
    val imdbID: String
)