package it.univpm.filmaccio.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.DiscoverMoviesResponse
import it.univpm.filmaccio.data.models.Movie

class MovieRepository {

    private val tmdbApi = TmdbApiClient.TMDB_API
    suspend fun getNowPlayingMovies(
        page: Int = 1,
        language: String = "it-IT",
        region: String = "IT"
    ): DiscoverMoviesResponse {
        return tmdbApi.getNowPlayingMovies(page = page, language = language, region = region)
    }

    suspend fun getTrendingMovies(language: String = "it-IT"): DiscoverMoviesResponse {
        return tmdbApi.getTrendingMovies(language = language)
    }

    suspend fun getTopRatedMovies(
        page: Int = 1,
        language: String = "it-IT",
        region: String = "IT"
    ): DiscoverMoviesResponse {
        return tmdbApi.getTopRatedMovies(page = page, language = language, region = region)
    }

    suspend fun getMovieDetails(movieId: Int): Movie {
        return tmdbApi.getMovieDetails(movieId = movieId)
    }
}