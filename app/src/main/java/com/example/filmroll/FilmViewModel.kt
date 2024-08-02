package com.example.filmroll

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmsdata.ApiResult
import com.example.filmsdata.DominantColors
import com.example.filmsdata.GetPaletteUseCase
import com.example.filmsdata.GetRandomFilmUseCase
import com.example.filmsdata.models.RandomFilm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmViewModel @Inject constructor(
    private val getRandomFilmUseCase: GetRandomFilmUseCase,
    private val getPaletteUseCase: GetPaletteUseCase
) : ViewModel() {
    private val _randomFilm = MutableStateFlow<RandomFilmViewState>(value = RandomFilmViewState.Loading)
    val randomFilm: StateFlow<RandomFilmViewState> = _randomFilm

    private val _dominantColor = MutableStateFlow(Color.Transparent)
    val dominantColor: StateFlow<Color> = _dominantColor

    private val _dominantColors = MutableStateFlow(defaultDominantColors)
    val dominantColors: StateFlow<DominantColors> = _dominantColors

    fun getRandomFilm() = viewModelScope.launch {
        _randomFilm.value = RandomFilmViewState.Loading
        getRandomFilmUseCase().collect { result ->
            val film = (result as? ApiResult.Success)?.data
            if (film != null) {
                val color = getPaletteUseCase(film.poster.url)
                _dominantColor.value = color.dominant
                _dominantColors.value = color
            }
            _randomFilm.value = result.fold(
                onSuccess = { RandomFilmViewState.Success(it) },
                onFailure = { RandomFilmViewState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun loadNextRandomFilm() {
        getRandomFilm()
    }

    companion object {
        val defaultDominantColors = DominantColors(
            dominant = Color.Transparent,
            vibrant = Color(0xFFff4081).toArgb(),
            darkVibrant = Color(0xFFff80ab).toArgb(),
            lightVibrant = Color(0xFFff6f00).toArgb(),
            muted = Color(0xFFff9100).toArgb(),
            darkMuted = Color(0xFFff9100).toArgb(),
            lightMuted = Color(0xFFff6f00).toArgb()
        )
    }
}


sealed interface RandomFilmViewState {
    object Loading: RandomFilmViewState
    data class  Error(val message: String): RandomFilmViewState
    data class Success(val film: RandomFilm ): RandomFilmViewState
}