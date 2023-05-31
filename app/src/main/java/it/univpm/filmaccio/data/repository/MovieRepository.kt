package it.univpm.filmaccio.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.data.models.DiscoverMoviesResponse

class MovieRepository {
    private val tmdbApi = TmdbApiClient.TMDB_API

    private val _nowPlayingMovies = MutableLiveData<List<Movie>>()
    private val _trendingMovies = MutableLiveData<List<Movie>>()
    private val _topRatedMovies = MutableLiveData<List<Movie>>()
    val nowPlayingMovies : LiveData<List<Movie>> get() = _nowPlayingMovies
    val trendingMovies : LiveData<List<Movie>> get() = _trendingMovies
    val topRatedMovies: LiveData<List<Movie>> get() = _topRatedMovies

    suspend fun getNowPlayingMovies(page: Int = 1, language: String = "it-IT", region: String = "IT"): DiscoverMoviesResponse {
        return tmdbApi.getNowPlayingMovies(page=page, language=language, region=region)
    }

    suspend fun getTrendingMovies(language: String = "it-IT"): DiscoverMoviesResponse {
        return tmdbApi.getTrendingMovies(language=language)
    }

    suspend fun getTopRatedMovies(page: Int = 1, language: String = "it-IT", region: String = "IT"): DiscoverMoviesResponse {
        return tmdbApi.getTopRatedMovies(page=page, language=language, region=region)
    }
}