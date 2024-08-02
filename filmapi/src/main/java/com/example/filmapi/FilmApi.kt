package com.example.filmapi

import android.content.Context
import com.example.filmapi.models.RandomFilmDTO
import com.example.filmapi.utils.FilmApiKeyInterceptor
import com.example.news.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import javax.inject.Singleton

/**
 * [API Documentation](https://api.kinopoisk.dev/documentation)
 */

interface FilmApi {
    @GET("movie/random?rating.kp=7-10")
    suspend fun randomMovie(): RandomFilmDTO
}

class FilmApiProviderN {
    fun provideFilmApi(): FilmApi {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(FilmApiKeyInterceptor(BuildConfig.API_KEY))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(FilmApi::class.java)
    }
}
