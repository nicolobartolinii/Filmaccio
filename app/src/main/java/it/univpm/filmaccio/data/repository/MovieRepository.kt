package it.univpm.filmaccio.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.DiscoverMoviesResponse
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take

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

    fun addToList(userId: String, listName: String, movieId: Int) {
        FirestoreService.addToList(userId, listName, movieId)
    }

    fun removeFromList(userId: String, listName: String, movieId: Int) {
        FirestoreService.removeFromList(userId, listName, movieId)
    }

    suspend fun isMovieWatched(userId: String, movieId: Int): Boolean {
        val watchedMovies: List<Any> = FirestoreService.getList(userId, "watched_m").first()
        return movieId.toLong() in watchedMovies
    }

    suspend fun isMovieInWatchlist(userId: String, movieId: Int): Boolean {
        val watchlistMovies: List<Any> = FirestoreService.getList(userId, "watchlist_m").first()
        return movieId.toLong() in watchlistMovies
    }

    suspend fun isMovieFavorited(userId: String, movieId: Int): Boolean {
        val favoriteMovies: List<Any> = FirestoreService.getList(userId, "favorite_m").first()
        return movieId.toLong() in favoriteMovies
    }
}