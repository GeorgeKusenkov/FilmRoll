package com.example.filmapi.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RatingDTO(
    @SerialName("kp") var kp: Float,
)
