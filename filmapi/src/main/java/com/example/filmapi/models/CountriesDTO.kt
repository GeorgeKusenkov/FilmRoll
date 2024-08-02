package com.example.filmapi.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CountriesDTO (
    @SerialName("name" ) var name: String
)
