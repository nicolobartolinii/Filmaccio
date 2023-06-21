package it.univpm.filmaccio.data.repository

import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.DiscoverSeriesResponse
import it.univpm.filmaccio.data.models.ProfileListItem
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.flow.first

// Per la spiegazione di questa classe vedere MovieRepository.kt che è molto simile
class SeriesRepository {
    private val tmdbApi = TmdbApiClient.TMDB_API

    suspend fun getTrendingSeries(language: String = "it-IT"): DiscoverSeriesResponse {
        return tmdbApi.getTrendingSeries(language = language)
    }

    suspend fun getTopRatedSeries(
        page: Int = 1,
        language: String = "it-IT",
        region: String = "IT"
    ): DiscoverSeriesResponse {
        return tmdbApi.getTopRatedSeries(page = page, language = language, region = region)
    }

    suspend fun getSeriesDetails(seriesId: Long): Series {
        val series = tmdbApi.getSeriesDetails(seriesId = seriesId)
        series.seasons = series.seasons.map {
            tmdbApi.getSeasonDetails(seriesId = seriesId, seasonNumber = it.number)
        }
        return series
    }

    fun addToList(userId: String, listName: String, seriesId: Long) {
        FirestoreService.addToList(userId, listName, seriesId)
    }

    fun removeFromList(userId: String, listName: String, seriesId: Long) {
        FirestoreService.removeFromList(userId, listName, seriesId)
    }

    suspend fun isSeriesInWatching(userId: String, seriesId: Long): Boolean {
        val watchingSeries: List<Any> = FirestoreService.getList(userId, "watching_t").first()
        return seriesId in watchingSeries
    }

    suspend fun isSeriesInWatchlist(userId: String, seriesId: Long): Boolean {
        val watchlistSeries: List<Any> = FirestoreService.getList(userId, "watchlist_t").first()
        return seriesId in watchlistSeries
    }

    suspend fun isSeriesFavorited(userId: String, seriesId: Long): Boolean {
        val favoriteSeries: List<Any> = FirestoreService.getList(userId, "favorite_t").first()
        return seriesId in favoriteSeries
    }

    suspend fun convertIdToProfileListItem(id1: Long, id2: Long, id3: Long, listTitle: String): ProfileListItem {
        val listName = when(listTitle) {
            "watching_t" -> "in visione (TV)__"
            "watchlist_t" -> "watchlist (TV)__"
            "favorite_t" -> "preferiti (TV)__"
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
        val movie1 = getSeriesDetails(id1)
        if (id2 == 0L) {
            return ProfileListItem(
                title = listName.substring(0, listName.length - 2).uppercase(),
                imageURL1 = movie1.posterPath ?: "",
                imageURL2 = "",
                imageURL3 = ""
            )
        }
        val movie2 = getSeriesDetails(id2)
        if (id3 == 0L) {
            return ProfileListItem(
                title = listName.substring(0, listName.length - 2).uppercase(),
                imageURL1 = movie1.posterPath ?: "",
                imageURL2 = movie2.posterPath ?: "",
                imageURL3 = ""
            )
        }
        val movie3 = getSeriesDetails(id3)
        return ProfileListItem(
            title = listName.substring(0, listName.length - 2).uppercase(),
            imageURL1 = movie1.posterPath ?: "",
            imageURL2 = movie2.posterPath ?: "",
            imageURL3 = movie3.posterPath ?: ""
        )
    }
}