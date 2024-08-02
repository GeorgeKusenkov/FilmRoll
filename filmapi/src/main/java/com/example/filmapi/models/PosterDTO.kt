package com.example.filmapi.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosterDTO(
    @SerialName("url") var url: String,
    @SerialName("previewUrl") var previewUrl: String
)
