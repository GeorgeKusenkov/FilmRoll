package com.example.filmapi.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RandomFilmDTO(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("rating") val rating: RatingDTO,
    @SerialName("description") val description: String,
    @SerialName("year") val year: Int,
    @SerialName("poster") val poster: PosterDTO,
    @SerialName("genres") val genres: List<GenresDTO>,
    @SerialName("countries") val countries: List<CountriesDTO>,
)


