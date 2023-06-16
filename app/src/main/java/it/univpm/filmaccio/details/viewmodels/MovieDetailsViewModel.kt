package it.univpm.filmaccio.details.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import it.univpm.filmaccio.data.repository.MovieRepository
import kotlinx.coroutines.Dispatchers

class MovieDetailsViewModel(private var movieId: Int = 0) : ViewModel() {
    private val movieRepository = MovieRepository()

    val currentMovie = liveData(Dispatchers.IO) {
        val movie = movieRepository.getMovieDetails(movieId)
        emit(movie)
    }
}

class MovieDetailsViewModelFactory(private val movieId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieDetailsViewModel(movieId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}