package com.example.filmsdata

import com.example.filmapi.models.RandomFilmDTO
import com.example.filmsdata.models.RandomFilm

internal fun RandomFilmDTO.toDomainRandomFilm(): RandomFilm {
    return RandomFilm (
        id = id,
        name = name,
        rating = rating.kp,
        description = description,
        year = year,
        poster = poster,
        genres = genres.map { it.name },
        countries = countries.map { it.name }
    )
}