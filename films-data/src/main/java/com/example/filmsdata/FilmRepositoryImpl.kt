package com.example.filmsdata

import android.content.Context
import com.example.filmapi.FilmApi
import com.example.filmsdata.models.RandomFilm
import coil.ImageLoader
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FilmRepositoryImpl @Inject constructor(private val filmApi: FilmApi): FilmRepository {
    override suspend fun getRandomFilm(): Flow<ApiResult<RandomFilm>> = flow {
        emit(ApiResult.safeApiCall { filmApi.randomMovie().toDomainRandomFilm() })
    }
}

class GetRandomFilmUseCase @Inject constructor(private val filmRepository: FilmRepository) {
    suspend operator fun invoke(): Flow<ApiResult<RandomFilm>> = filmRepository.getRandomFilm()
}

class GetPaletteUseCase @Inject constructor(private val colorExtractor: ColorExtractor) {
    suspend operator fun invoke(imageUrl: String): DominantColors = colorExtractor.getDominantColor(imageUrl)
}

class ColorExtractor @Inject constructor(private val context: Context) {
    suspend fun getDominantColor(imageUrl: String): DominantColors = withContext(Dispatchers.IO) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .build()

        val result = (loader.execute(request) as SuccessResult).drawable
        val bitmap = result.toBitmap()

        suspendCoroutine { continuation ->
            Palette.from(bitmap).generate { palette ->
                val dominantColors = DominantColors(
                    dominant = palette?.dominantSwatch?.rgb?.let { Color(it) } ?: Color.Transparent,
                    vibrant = palette?.getVibrantColor(Color.Transparent.toArgb()) ?: Color.Transparent.toArgb(),
                    darkVibrant = palette?.getDarkVibrantColor(Color.Transparent.toArgb()) ?: Color.Transparent.toArgb(),
                    lightVibrant = palette?.getLightVibrantColor(Color.Transparent.toArgb()) ?: Color.Transparent.toArgb(),
                    muted = palette?.getMutedColor(Color.Transparent.toArgb()) ?: Color.Transparent.toArgb(),
                    darkMuted = palette?.getDarkMutedColor(Color.Transparent.toArgb()) ?: Color.Transparent.toArgb(),
                    lightMuted = palette?.getLightMutedColor(Color.Transparent.toArgb()) ?: Color.Transparent.toArgb()
                )
                continuation.resume(dominantColors)
            }
        }

//        suspendCoroutine { continuation ->
//            Palette.from(bitmap).generate { palette ->
//                val dominantColor =
//                    palette?.dominantSwatch?.rgb?.let { Color(it) } ?: Color.Transparent
//                continuation.resume(dominantColor)
//            }
//        }
    }
}
//        val col = Color.Transparent

//        suspendCoroutine { continuation ->
//            Palette.from(bitmap).generate { palette ->
//                val dominantColors = DominantColors(
//                    vibrant = palette?.getVibrantColor(Color.Transparent.toArgb()) ,
//                    darkVibrant = palette?.getDarkVibrantColor(Color.Transparent.toArgb()),
//                    lightVibrant = palette?.getLightVibrantColor(Color.Transparent.toArgb()),
//                    muted = palette?.getMutedColor(Color.Transparent.toArgb()),
//                    darkMuted = palette?.getDarkMutedColor(Color.Transparent.toArgb()),
//                    lightMuted = palette?.getLightMutedColor(Color.Transparent.toArgb())
//                )
//                continuation.resume(dominantColors)
//            }
//        }
//    }
//}

data class DominantColors(
    val dominant: Color = Color.Transparent,
    val vibrant: Int = Color.Green.toArgb(),
    val darkVibrant: Int = Color.Green.toArgb(),
    val lightVibrant: Int = Color.Green.toArgb(),
    val muted: Int = Color.Green.toArgb(),
    val darkMuted: Int = Color.Green.toArgb(),
    val lightMuted: Int = Color.Green.toArgb()
)
