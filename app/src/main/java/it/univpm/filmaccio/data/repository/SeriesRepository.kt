package it.univpm.filmaccio.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.DiscoverSeriesResponse
import it.univpm.filmaccio.data.models.Series

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
}