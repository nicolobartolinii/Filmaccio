package it.univpm.filmaccio.data.repository

import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.DiscoverSeriesResponse
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.flow.first

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

    suspend fun getSeriesDetails(seriesId: Int): Series {
        val series = tmdbApi.getSeriesDetails(seriesId = seriesId)
        series.seasons = series.seasons.map {
            tmdbApi.getSeasonDetails(seriesId = seriesId, seasonNumber = it.number)
        }
        return series
    }

    fun addToList(userId: String, listName: String, seriesId: Int) {
        FirestoreService.addToList(userId, listName, seriesId)
    }

    fun removeFromList(userId: String, listName: String, seriesId: Int) {
        FirestoreService.removeFromList(userId, listName, seriesId)
    }

    suspend fun isSeriesInWatching(userId: String, seriesId: Int): Boolean {
        val watchingSeries: List<Any> = FirestoreService.getList(userId, "watching_t").first()
        return seriesId.toLong() in watchingSeries
    }

    suspend fun isSeriesInWatchlist(userId: String, seriesId: Int): Boolean {
        val watchlistSeries: List<Any> = FirestoreService.getList(userId, "watchlist_t").first()
        return seriesId.toLong() in watchlistSeries
    }

    suspend fun isSeriesFavorited(userId: String, seriesId: Int): Boolean {
        val favoriteSeries: List<Any> = FirestoreService.getList(userId, "favorite_t").first()
        return seriesId.toLong() in favoriteSeries
    }
}