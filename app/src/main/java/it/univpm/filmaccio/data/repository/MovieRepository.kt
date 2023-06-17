package it.univpm.filmaccio.data.repository

import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.DiscoverMoviesResponse
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.data.models.ProfileListItem
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.flow.first

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

    suspend fun getMovieDetails(movieId: Long): Movie {
        return tmdbApi.getMovieDetails(movieId = movieId)
    }

    fun addToList(userId: String, listName: String, movieId: Long) {
        FirestoreService.addToList(userId, listName, movieId)
    }

    fun removeFromList(userId: String, listName: String, movieId: Long) {
        FirestoreService.removeFromList(userId, listName, movieId)
    }

    suspend fun isMovieWatched(userId: String, movieId: Long): Boolean {
        val watchedMovies: List<Any> = FirestoreService.getList(userId, "watched_m").first()
        return movieId in watchedMovies
    }

    suspend fun isMovieInWatchlist(userId: String, movieId: Long): Boolean {
        val watchlistMovies: List<Any> = FirestoreService.getList(userId, "watchlist_m").first()
        return movieId in watchlistMovies
    }

    suspend fun isMovieFavorited(userId: String, movieId: Long): Boolean {
        val favoriteMovies: List<Any> = FirestoreService.getList(userId, "favorite_m").first()
        return movieId in favoriteMovies
    }

    suspend fun convertIdToProfileListItem(
        id1: Long,
        id2: Long,
        id3: Long,
        listTitle: String
    ): ProfileListItem {
        val listName = when (listTitle) {
            "watched_m" -> "visti (film)__"
            "watchlist_m" -> "watchlist (film)__"
            "favorite_m" -> "preferiti (film)__"
            else -> listTitle
        }
        if (id1 == 0L) {
            return ProfileListItem(
                title = listName.substring(0, listName.length - 2).uppercase(),
                imageURL1 = "",
                imageURL2 = "",
                imageURL3 = ""
            )
        }
        val movie1 = getMovieDetails(id1)
        if (id2 == 0L) {
            return ProfileListItem(
                title = listName.substring(0, listName.length - 2).uppercase(),
                imageURL1 = movie1.posterPath ?: "",
                imageURL2 = "",
                imageURL3 = ""
            )
        }
        val movie2 = getMovieDetails(id2)
        if (id3 == 0L) {
            return ProfileListItem(
                title = listName.substring(0, listName.length - 2).uppercase(),
                imageURL1 = movie1.posterPath ?: "",
                imageURL2 = movie2.posterPath ?: "",
                imageURL3 = ""
            )
        }
        val movie3 = getMovieDetails(id3)
        return ProfileListItem(
            title = listName.substring(0, listName.length - 2).uppercase(),
            imageURL1 = movie1.posterPath ?: "",
            imageURL2 = movie2.posterPath ?: "",
            imageURL3 = movie3.posterPath ?: ""
        )
    }
}