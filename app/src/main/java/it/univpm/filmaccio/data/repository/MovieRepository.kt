package it.univpm.filmaccio.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.api.TmdbApiService
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.data.models.MovieResponse

class MovieRepository {
    private val tmdbApi = TmdbApiClient.TMDB_API

    private val _nowPlayingMovies = MutableLiveData<List<Movie>>()
    val nowPlayingMovies : LiveData<List<Movie>> get() = _nowPlayingMovies

    suspend fun getNowPlayingMovies(page: Int = 1, language: String = "it-IT", region: String = "IT"): MovieResponse {
        return tmdbApi.getNowPlayingMovies()
    }
}