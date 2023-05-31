package it.univpm.filmaccio.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.DiscoverMoviesResponse
import it.univpm.filmaccio.data.models.DiscoverSeriesResponse
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.data.models.Series

class SeriesRepository {
    private val tmdbApi = TmdbApiClient.TMDB_API

    private val _trendingSeries = MutableLiveData<List<Series>>()
    private val _topRatedSeries = MutableLiveData<List<Series>>()
    val trendingSeries : LiveData<List<Series>> get() = _trendingSeries
    val topRatedSeries: LiveData<List<Series>> get() = _topRatedSeries

    suspend fun getTrendingSeries(language: String = "it-IT"): DiscoverSeriesResponse {
        return tmdbApi.getTrendingSeries(language=language)
    }

    suspend fun getTopRatedSeries(page: Int = 1, language: String = "it-IT", region: String = "IT"): DiscoverSeriesResponse {
        return tmdbApi.getTopRatedSeries(page=page, language=language, region=region)
    }
}