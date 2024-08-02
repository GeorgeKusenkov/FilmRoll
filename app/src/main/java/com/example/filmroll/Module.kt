package com.example.filmroll

import android.content.Context
import com.example.filmapi.FilmApi
import com.example.filmapi.FilmApiProviderN
import com.example.filmsdata.ColorExtractor
import com.example.filmsdata.FilmRepository
import com.example.filmsdata.FilmRepositoryImpl
import com.example.filmsdata.GetPaletteUseCase
import com.example.filmsdata.GetRandomFilmUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideFilmRepository(filmApi: FilmApi): FilmRepository {
        return FilmRepositoryImpl(filmApi)
    }

    @Provides
    @Singleton
    fun provideGetRandomFilmUseCase(filmRepository: FilmRepository): GetRandomFilmUseCase {
        return GetRandomFilmUseCase(filmRepository)
    }

    @Provides
    @Singleton
    fun provideGetPaletteUseCase(colorExtractor: ColorExtractor): GetPaletteUseCase {
        return GetPaletteUseCase(colorExtractor)
    }

    @Provides
    @Singleton
    fun provideColorExtractor(@ApplicationContext context: Context): ColorExtractor {
        return ColorExtractor(context)
    }

}

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideFilmApiProvider(): FilmApiProviderN {
        return FilmApiProviderN()
    }

    @Provides
    @Singleton
    fun provideFilmApi(filmApiProvider: FilmApiProviderN): FilmApi {
        return filmApiProvider.provideFilmApi()
    }
}