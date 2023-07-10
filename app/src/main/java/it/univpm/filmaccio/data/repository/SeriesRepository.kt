package it.univpm.filmaccio.data.repository

import com.google.firebase.Timestamp
import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.DiscoverSeriesResponse
import it.univpm.filmaccio.data.models.ImagesResponse
import it.univpm.filmaccio.data.models.ProfileListItem
import it.univpm.filmaccio.data.models.ReviewTriple
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.flow.first

/**
 * Questa classe è un repository che si occupa di gestire i dati relativi alle serie.
 * È molto simile a MovieRepository, quindi per la spiegazione vedere quella classe.
 *
 * @see MovieRepository
 *
 * @author nicolobartolinii
 */
class SeriesRepository {
    private val tmdbApi = TmdbApiClient.TMDB_API

    suspend fun getTrendingSeries(language: String = "it-IT"): DiscoverSeriesResponse {
        return tmdbApi.getTrendingSeries(language = language)
    }

    suspend fun getTopRatedSeries(
        page: Int = 1, language: String = "it-IT", region: String = "IT"
    ): DiscoverSeriesResponse {
        return tmdbApi.getTopRatedSeries(page = page, language = language, region = region)
    }

    suspend fun getSeriesDetails(seriesId: Long): Series {
        val series = tmdbApi.getSeriesDetails(seriesId = seriesId, language = "it-IT")
        if (seriesHasMissingDetails(series)) {
            val seriesInEnglish = tmdbApi.getSeriesDetails(seriesId = seriesId, language = "en-US")
            fillMissingDetails(series, seriesInEnglish)
        }
        series.seasons = series.seasons.map {
            val season = tmdbApi.getSeasonDetails(
                seriesId = seriesId, seasonNumber = it.number, language = "it-IT"
            )
            if (seasonHasMissingDetails(season)) {
                val seasonInEnglish = tmdbApi.getSeasonDetails(
                    seriesId = seriesId, seasonNumber = it.number, language = "en-US"
                )
                fillMissingSeasonDetails(season, seasonInEnglish)
            }
            season
        }
        return series
    }

    suspend fun getSeasonDetails(seriesId: Long, seasonNumber: Long): Series.Season {
        val season = tmdbApi.getSeasonDetails(
            seriesId = seriesId, seasonNumber = seasonNumber, language = "it-IT"
        )
        if (seasonHasMissingDetails(season)) {
            val seasonInEnglish = tmdbApi.getSeasonDetails(
                seriesId = seriesId, seasonNumber = seasonNumber, language = "en-US"
            )
            fillMissingSeasonDetails(season, seasonInEnglish)
        }
        return season
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

    suspend fun isSeriesFinished(userId: String, seriesId: Long): Boolean {
        val finishedSeries: List<Any> = FirestoreService.getList(userId, "finished_t").first()
        return seriesId in finishedSeries
    }

    suspend fun getSeriesImages(seriesId: Long): ImagesResponse {
        return tmdbApi.getSeriesImages(seriesId = seriesId)
    }

    suspend fun convertIdToProfileListItem(
        id1: Long, id2: Long, id3: Long, listTitle: String
    ): ProfileListItem {
        val listName = when (listTitle) {
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

    private fun seriesHasMissingDetails(series: Series): Boolean {
        return series.title.isEmpty() || series.overview.isEmpty()
    }

    @Suppress("BooleanMethodIsAlwaysInverted")
    private fun seasonHasMissingDetails(season: Series.Season): Boolean {
        return season.name.isEmpty() || season.overview.isEmpty() || season.episodes.any { it.name.isEmpty() || it.name == "Episodio ${it.number}" || it.overview.isEmpty() }
    }

    private fun fillMissingDetails(series: Series, seriesInEnglish: Series) {
        if (series.title.isEmpty()) series.title = seriesInEnglish.title
        if (series.overview.isEmpty()) series.overview = seriesInEnglish.overview
    }

    private fun fillMissingSeasonDetails(season: Series.Season, seasonInEnglish: Series.Season) {
        if (season.name.isEmpty()) season.name = seasonInEnglish.name
        if (season.overview.isEmpty()) season.overview = seasonInEnglish.overview
        season.episodes.forEachIndexed { index, episode ->
            if (episode.name.isEmpty()) episode.name =
                seasonInEnglish.episodes.getOrNull(index)?.name.toString()
            if (episode.name == "Episodio ${episode.number}") {
                if (seasonInEnglish.episodes.getOrNull(index)?.name.toString() != "Episode ${episode.number}") {
                    episode.name = seasonInEnglish.episodes.getOrNull(index)?.name.toString()
                }
            }
            if (episode.overview.isEmpty()) episode.overview =
                seasonInEnglish.episodes.getOrNull(index)?.overview.toString()
        }
    }

    suspend fun checkIfSeriesFinished(uid: String, seriesId: Long) {
        val watchingSeries = FirestoreService.getWatchingSeries(uid)
            .first()[seriesId.toString()] as MutableMap<String, List<Long>>
        watchingSeries.remove("0")
        val seriesSeasons = getSeriesDetails(seriesId).seasons as MutableList<Series.Season>
        seriesSeasons.removeIf { it.number == 0L }
        if (watchingSeries.size != seriesSeasons.size) {
            return
        } else {
            watchingSeries.forEach { (seasonNumber, episodes) ->
                if (episodes.size != seriesSeasons[seasonNumber.toInt() - 1].episodes.size) {
                    val finishedSeries = FirestoreService.getList(uid, "finished_t").first()
                    if (seriesId in finishedSeries) {
                        FirestoreService.removeFromList(uid, "finished_t", seriesId)
                        FirestoreService.addToList(uid, "watching_t", seriesId)
                    }
                    return
                }
            }
            FirestoreService.removeFromList(uid, "watching_t", seriesId)
            FirestoreService.addToList(uid, "finished_t", seriesId)
        }
    }

    suspend fun isSeriesRated(userId: String, seriesId: Long): Boolean {
        val seriesRating = FirestoreService.getSeriesRating(userId, seriesId).first
        return seriesRating != 0f
    }

    suspend fun getSeriesRating(userId: String, seriesId: Long): Pair<Float, Timestamp> {
        return FirestoreService.getSeriesRating(userId, seriesId)
    }

    suspend fun isSeriesReviewed(userId: String, seriesId: Long): Boolean {
        val seriesReview = FirestoreService.getSeriesReview(userId, seriesId).first
        return seriesReview != ""
    }

    suspend fun getSeriesReview(userId: String, seriesId: Long): Pair<String, Timestamp> {
        return FirestoreService.getSeriesReview(userId, seriesId)
    }

    suspend fun updateSeriesRating(
        userId: String, seriesId: Long, rating: Float, timestamp: Timestamp
    ) {
        FirestoreService.updateSeriesRating(userId, seriesId, rating, timestamp)
    }

    suspend fun updateSeriesReview(
        userId: String, seriesId: Long, review: String, timestamp: Timestamp
    ) {
        FirestoreService.updateSeriesReview(userId, seriesId, review, timestamp)
    }

    suspend fun getAverageSeriesRating(seriesId: Long): Float {
        return FirestoreService.getAverageSeriesRating(seriesId)
    }

    suspend fun getSeriesReviews(seriesId: Long): List<ReviewTriple> {
        return FirestoreService.getSeriesReviews(seriesId)
    }

    suspend fun getUserReviews(userId: String): Pair<ReviewTriple, Long>? {
        return FirestoreService.getUserReviews(userId, "series")
    }
}