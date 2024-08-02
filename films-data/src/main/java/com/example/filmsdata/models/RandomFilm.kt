package com.example.filmsdata.models

import com.example.filmapi.models.PosterDTO

data class RandomFilm (
    val id: Int,
    val name: String,
    val rating: Float,
    val description: String = "Описание отсутсвует",
    val year: Int,
    val poster: PosterDTO,
    val genres: List<String>,
    val countries: List<String>,
)