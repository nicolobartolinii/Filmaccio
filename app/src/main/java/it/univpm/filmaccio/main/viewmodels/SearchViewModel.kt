package it.univpm.filmaccio.main.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import kotlinx.coroutines.Dispatchers

class SearchViewModel : ViewModel() {
    private val movieRepository = MovieRepository()
    private val seriesRepository = SeriesRepository()

    val trendingMovies = liveData(Dispatchers.IO) {
        val movies = movieRepository.getTrendingMovies()
        emit(movies)
    }

    val trendingSeries = liveData(Dispatchers.IO) {
        val series = seriesRepository.getTrendingSeries()
        emit(series)
    }

    val topRatedMovies = liveData(Dispatchers.IO) {
        val movies = movieRepository.getTopRatedMovies()
        emit(movies)
    }

    val topRatedSeries = liveData(Dispatchers.IO) {
        val series = seriesRepository.getTopRatedSeries()
        emit(series)
    }
}